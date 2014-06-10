package controllers

import com.google.gson.Gson
import play.api.Logger
import play.api.mvc.{Action, Controller}
import neo4j.Neo4JServiceProvider
import org.springframework.data.domain.Sort
import neo4j.models.{NeoTrackParts, CarType, ImageNeoNode, NeoTrack}
import play.api.templates.Html
import plugins.jsAnnotations.JSRoute
import scala.collection.JavaConverters._
import play.api.i18n.Messages
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * Created by tuxburner on 5/24/14.
 */
object DataController extends Controller {


  /**
   * The main view when the user clicks on the data option
   * @return
   */
  def mainDataView(tab: String) = Action {
    implicit request =>
      Ok(views.html.data.mainview.render(tab, (request)));
  }

  /**
   * Displays the add for the given type form
   * @return
   */
  @JSRoute
  def displayAddForm(formType: String) = Action {
    formType match {
      case "drivers" => Ok(views.html.data.driver.addDriver(Forms.ImageNameForm))
      case "cars" => Ok(views.html.data.car.addCar(Forms.CarForm))
      case "tracks" => Ok(views.html.data.track.addTrack(Forms.TrackForm))
    }

  }

  /**
   * Loads all tracks from the backend
   * @return
   */
  @JSRoute
  def listTracks = Neo4jTransactionAction {
    val tracks = Neo4JServiceProvider.get().trackRepo.findAll(new Sort("name")).asScala.toList;
    Ok(views.html.data.track.trackView(tracks))
  }

  /**
   * Adds a new track
   * @return
   */
  @JSRoute
  def addTrack() = Action {
    implicit request =>
      Forms.TrackForm.bindFromRequest().fold(
        formWithErrors => {
          Redirect(routes.DataController.mainDataView("tracks")).flashing(BaseController.FLASH_ERROR_KEY -> "track.add.error");
        },
        value => {
          val neoTrack = new NeoTrack
          neoTrack.name = value.name
          Neo4JServiceProvider.get().trackRepo.save(neoTrack)
          Redirect(routes.DataController.mainDataView("tracks")).flashing(BaseController.FLASH_SUCCESS_KEY -> Messages("track.add.success", neoTrack.name));
        }
      )
  }

  /**
   * Displays the edit page for the track
   * @param id
   * @return
   */
  @JSRoute
  def displayEditTrack(id: Long) = Neo4jTransactionAction {
    val neoTrack = Neo4JServiceProvider.get().trackRepo.findOne(id);
    val form = Forms.TrackForm.fill(Track.apply(neoTrack.name));
    Ok(views.html.data.track.editTrack(id, form))
  }

  /**
   * Edits the track
   * @param id
   * @return
   */
  def editTrack(id: Long) = Neo4jTransactionAction {
    implicit request =>
      Forms.TrackForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.DataController.mainDataView("tracks")).flashing(BaseController.FLASH_ERROR_KEY -> "track.edit.error");
        },
        value => {
          val neoTrack = Neo4JServiceProvider.get().trackRepo.findOne(id);
          neoTrack.name = value.name
          Neo4JServiceProvider.get().trackRepo.save(neoTrack);
          Redirect(routes.DataController.mainDataView("tracks")).flashing(BaseController.FLASH_SUCCESS_KEY -> Messages("track.edit.success", neoTrack.name));
        }
      )
  }

  def displayTrackEditor(id: Long) = Neo4jTransactionAction {
    implicit request =>
      val neoTrack = Neo4JServiceProvider.get().trackRepo.findOne(id)
      if(neoTrack == null) {
        Redirect(routes.DataController.mainDataView("tracks"))
      } else {
        val neoTrackParts = Neo4JServiceProvider.get().trackPartsRepo.findAll().asScala.headOption.getOrElse(new NeoTrackParts);

        val trackPartsAsJson = new Html(new StringBuilder(new Gson().toJson(Forms.neoTrackPartsToTrackParts(neoTrackParts))));
        Ok(views.html.data.track.trackEditor(neoTrack,trackPartsAsJson))
      }
  }

  /**
   * Lists all drivers
   * @return
   */
  @JSRoute
  def listDrivers = Neo4jTransactionAction {
    val drivers = Neo4JServiceProvider.get().driverRepo.findAll(new Sort("name")).iterator().asScala.toList;
    Ok(views.html.data.driver.driverView(drivers))
  }


  /**
   * Adds the driver to the database
   * @return
   */
  def addDriver = Neo4jTransactionAction {
    implicit request =>

      Forms.ImageNameForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.DataController.mainDataView("drivers")).flashing(BaseController.FLASH_ERROR_KEY -> "driver.add.error");
        },
        value => {
          val driverForm = Forms.ImageNameForm.bindFromRequest()
          val neoDriver = new neo4j.models.NeoDriver();
          neoDriver.name = driverForm.get.name;

          val neoDriverSaved = Neo4JServiceProvider.get().driverRepo.save(neoDriver);
          saveImageData(neoDriverSaved, driverForm.get.imageData);
          Redirect(routes.DataController.mainDataView("drivers")).flashing(BaseController.FLASH_SUCCESS_KEY -> Messages("driver.add.success", neoDriver.name));
        }
      )
  }

  /**
   * Displays the edit page for the driver
   * @param id
   * @return
   */
  @JSRoute
  def displayEditDriver(id: Long) = Neo4jTransactionAction {
    val neoDriver = Neo4JServiceProvider.get().driverRepo.findOne(id);
    val form = Forms.ImageNameForm.fill(ImageName.apply(neoDriver.name, getImageDataForNode(neoDriver)));
    Ok(views.html.data.driver.editDriver(id, form))
  }

  /**
   * Edits the driver
   * @param id
   * @return
   */
  def editDriver(id: Long) = Neo4jTransactionAction {
    implicit request =>

      Forms.ImageNameForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.DataController.mainDataView("drivers")).flashing(BaseController.FLASH_ERROR_KEY -> "driver.edit.error");
        },
        value => {
          val neoDriver = Neo4JServiceProvider.get().driverRepo.findOne(id);
          neoDriver.name = value.name
          //val neoDriverSaved = Neo4JServiceProvider.get().driverRepo.save(neoDriver);
          saveImageData(neoDriver, value.imageData);
          Redirect(routes.DataController.mainDataView("drivers")).flashing(BaseController.FLASH_SUCCESS_KEY -> Messages("driver.edit.success", neoDriver.name));
        }
      )
  }

  /**
   * Lists all cars
   * @return
   */
  @JSRoute
  def listCars = Neo4jTransactionAction {
    val cars = Neo4JServiceProvider.get().carRepo.findAll(new Sort("name")).iterator().asScala.toList;
    Ok(views.html.data.car.carView(cars))
  }


  /**
   * Adds the car to the database
   * @return
   */
  def addCar = Neo4jTransactionAction {
    implicit request =>

      Forms.CarForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.DataController.mainDataView("cars")).flashing(BaseController.FLASH_ERROR_KEY -> "car.add.error");
        },
        value => {
          val neoCar = new neo4j.models.NeoCar();
          neoCar.name = value.imageName.name;
          neoCar.carType = CarType.valueOf(value.carType);
          val neoCarSaved = Neo4JServiceProvider.get().carRepo.save(neoCar);
          saveImageData(neoCarSaved, value.imageName.imageData);
          Redirect(routes.DataController.mainDataView("cars")).flashing(BaseController.FLASH_SUCCESS_KEY -> Messages("car.add.success", neoCar.name));
        }
      )
  }

  /**
   * Displays the edit page for the car
   * @param id
   * @return
   */
  @JSRoute
  def displayEditCar(id: Long) = Neo4jTransactionAction {
    val neoCar = Neo4JServiceProvider.get().carRepo.findOne(id);
    val form = Forms.CarForm.fill(Car.apply(neoCar.carType.name(), ImageName.apply(neoCar.name, getImageDataForNode(neoCar))));
    Ok(views.html.data.car.editCar(id, form))
  }

  /**
   * Edits the car
   * @param id
   * @return
   */
  def editCar(id: Long) = Neo4jTransactionAction {
    implicit request =>

      Forms.CarForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.DataController.mainDataView("cars")).flashing(BaseController.FLASH_ERROR_KEY -> "car.edit.error");
        },
        value => {
          val neoCar = Neo4JServiceProvider.get().carRepo.findOne(id);
          neoCar.name = value.imageName.name
          neoCar.carType = CarType.valueOf(value.carType)
          //val neoCarSaved = Neo4JServiceProvider.get().carRepo.save(neoCar);
          saveImageData(neoCar, value.imageName.imageData);
          Redirect(routes.DataController.mainDataView("cars")).flashing(BaseController.FLASH_SUCCESS_KEY -> Messages("car.edit.success", neoCar.name));
        }
      )
  }

  /**
   * Action for editing the trackparts
   * @return
   */
  @JSRoute
  def displayEditTrackParts() = Neo4jTransactionAction {
    implicit request =>
      val neoTrackParts = Neo4JServiceProvider.get().trackPartsRepo.findAll();
      val trackParts = neoTrackParts.asScala.headOption.getOrElse(new NeoTrackParts())
      val form = Forms.TrackPartsForm.fill(new TrackParts(trackParts.straight,trackParts.dblStraight,trackParts.fourthStraight,trackParts.thirdStraight,trackParts.curve90,trackParts.curve45,trackParts.curve452, trackParts.connectStraight));
      Ok(views.html.data.trackParts.render(form))
  }

  /**
   * Edits the trackparts
   * @return
   */
  def editTrackParts()  = Neo4jTransactionAction {
    implicit request =>
      Forms.TrackPartsForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.DataController.mainDataView("trackParts")).flashing(BaseController.FLASH_ERROR_KEY -> "trackParts.edit.error");
        },
        value => {
          val neoTrackParts = Neo4JServiceProvider.get().trackPartsRepo.findAll();
          val trackParts = neoTrackParts.asScala.headOption.getOrElse(new NeoTrackParts())
          trackParts.connectStraight = value.connectStraight
          trackParts.straight = value.straight
          trackParts.dblStraight = value.dblStraight
          trackParts.fourthStraight = value.fourthStraight
          trackParts.thirdStraight = value.thirdStraight
          trackParts.curve90 = value.curve90
          trackParts.curve45 = value.curve45
          trackParts.curve452 = value.curve452
          Neo4JServiceProvider.get().trackPartsRepo.save(trackParts)

          Redirect(routes.DataController.mainDataView("trackParts")).flashing(BaseController.FLASH_SUCCESS_KEY -> Messages("trackParts.edit.success"));
        }
      )

  }

  /**
   * Gets the image data for the given node
   * @param node
   * @return
   */
  def getImageDataForNode(node: ImageNeoNode): String = {
    node.hasPicture.booleanValue() match {
      case true => getImageDataAsBas64String(node.id)
      case false => ""
    }
  }

  /**
   * Streams the image
   * @param id
   * @return
   */
  def displayImage(id: Long) = Action {
    val imageData = FileUtils.readFileToByteArray(new File("dataImages/" + id))
    Ok(imageData)
  }

  /**
   * Encodes the image data to a base64 string
   * @param id
   * @return
   */
  def getImageDataAsBas64String(id: Long): String = {
    Base64.encodeBase64String(FileUtils.readFileToByteArray(new File("dataImages/" + id)))
  }

  /**
   * Saves the base64 encoded image with the id of the node to the filesystem
   * @param node
   * @param imageData
   */
  def saveImageData(node: ImageNeoNode, imageData: String) {

    if (node.id == null) {
      return
    }
    if (imageData.isEmpty == true) {
      node.hasPicture = false;
    } else {
      node.hasPicture = true;
      val imageDataBytes = Base64.decodeBase64(imageData);
      FileUtils.writeByteArrayToFile(new File("dataImages/" + node.id), imageDataBytes, false);
    }
    Neo4JServiceProvider.get().template.save(node);
  }

}
