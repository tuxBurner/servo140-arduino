package controllers

import play.api.mvc.{Action, Controller}
import neo4j.Neo4JServiceProvider
import org.springframework.data.domain.Sort
import neo4j.models.{CarType, ImageNeoNode, NeoTrack}
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
      Ok(views.html.data.mainview.render(tab,(request)));
  }

  /**
   * Loads all tracks from the backend
   * @return
   */
  @JSRoute
  def listTracks = Action {
    Neo4JServiceProvider.get().trackRepo.findAll(new Sort("name"));
    Ok("")
  }

  /**
   * Adds a new track
   * @param name
   * @return
   */
  @JSRoute
  def addTrack(name: String) = Action {
    val track = new NeoTrack();
    track.name = name;
    Neo4JServiceProvider.get().trackRepo.save(track);
    Ok("");
  }

  /**
   * Lists all drivers
   * @return
   */
  @JSRoute
  def listDrivers = Neo4jTransactionAction {
    val drivers = Neo4JServiceProvider.get().driverRepo.findAll(new Sort("name")).iterator().asScala.toList;
    Ok(views.html.data.driverView(drivers))
  }


  /**
   * Displays the add driver form
   * @return
   */
  @JSRoute
  def displayAddDriver = Action {
    Ok(views.html.data.addDriver(Forms.DriverForm))
  }

  /**
   * Adds the driver to the database
   * @return
   */
  def addDriver = Neo4jTransactionAction {
    implicit request =>

      Forms.DriverForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.DataController.mainDataView("drivers")).flashing(BaseController.FLASH_ERROR_KEY -> "driver.add.error");
        },
        value => {
          val driverForm = Forms.DriverForm.bindFromRequest()
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
    val form = Forms.DriverForm.fill(Driver.apply(neoDriver.name, getImageDataForNode(neoDriver)));
    Ok(views.html.data.editDriver(id, form))
  }

  /**
   * Edits the driver
   * @param id
   * @return
   */
  def editDriver(id: Long) = Neo4jTransactionAction {
    implicit request =>

      Forms.DriverForm.bindFromRequest.fold(
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
    Ok(views.html.data.carView(cars))
  }

  /**
   * Displays the add cars form
   * @return
   */
  @JSRoute
  def displayAddCar = Action {
    Ok(views.html.data.addCar(Forms.CarForm))
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
    val form = Forms.CarForm.fill(Car.apply(neoCar.carType.name(), Driver.apply(neoCar.name,getImageDataForNode(neoCar))));
    Ok(views.html.data.editCar(id, form))
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
