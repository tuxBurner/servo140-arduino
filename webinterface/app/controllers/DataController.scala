package controllers

import play.api.mvc.{Action, Controller}
import neo4j.Neo4JServiceProvider
import org.springframework.data.domain.Sort
import neo4j.models.{ImageNeoNode, NeoTrack}
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
  def mainDataView = Action {
    implicit request =>
      Ok(views.html.data.mainview.render((request)));
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
          Redirect(routes.DataController.mainDataView).flashing(BaseController.FLASH_ERROR_KEY -> "driver.add.error");
        },
        value => {
          val driverForm = Forms.DriverForm.bindFromRequest()
          val neoDriver = new neo4j.models.NeoDriver();
          neoDriver.name = driverForm.get.name;

          val neoDriverSaved = Neo4JServiceProvider.get().driverRepo.save(neoDriver);
          saveImageData(neoDriverSaved, driverForm.get.imageData);
          Redirect(routes.DataController.mainDataView).flashing(BaseController.FLASH_SUCCESS_KEY -> Messages("driver.add.success", neoDriver.name));
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

    val imageData = neoDriver.hasPicture.booleanValue() match {
      case true => getImageDataAsBas64String(id)
      case false => ""
    }


    val form = Forms.DriverForm.fill(Driver.apply(neoDriver.name, imageData));

    Ok(views.html.data.editDriver(id, form))
  }

  /**
   * Displays the edit page for the driver
   * @param id
   * @return
   */
  def editDriver(id: Long) = Neo4jTransactionAction {
    implicit request =>

      Forms.DriverForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.DataController.mainDataView).flashing(BaseController.FLASH_ERROR_KEY -> "driver.edit.error");
        },
        value => {
          val driverForm = Forms.DriverForm.bindFromRequest()
          val neoDriver = Neo4JServiceProvider.get().driverRepo.findOne(id);
          neoDriver.name = driverForm.get.name;

          val neoDriverSaved = Neo4JServiceProvider.get().driverRepo.save(neoDriver);
          saveImageData(neoDriverSaved, driverForm.get.imageData);
          Redirect(routes.DataController.mainDataView).flashing(BaseController.FLASH_SUCCESS_KEY -> Messages("driver.edit.success", neoDriver.name));
        }
      )
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
