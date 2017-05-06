package controllers

import javax.inject.Inject

import daos.PowerStationDao
import models.{CreatePowerStation, PowerStation}
import play.api.libs.json._
import play.api.mvc.Controller
import services.{PowerStationService, UserService}

import scala.concurrent.ExecutionContext.Implicits.global

class PowerStationController @Inject() (userService: UserService, powerStationDao: PowerStationDao, powerStationService: PowerStationService) extends Controller{

  def createPowerStation = AuthenticatedAction(userService).async(parse.json) { request =>
    ParseAction.parseJsonBody[CreatePowerStation](request.body) { powerStation =>
      powerStationService.createPowerStation(powerStation, request.user.id)
        .map(powerStation => Ok(Json.toJson(powerStation)(PowerStation.powerStationWrites)))
    }
  }
    def deletePowerStation(powerStationId: Long) = AuthenticatedAction(userService).async(parse.empty){ request =>
      powerStationDao.delete(powerStationId, request.user.id)
        .map(numberOfDeletes => {
          if(numberOfDeletes == 0) NotFound
          else NoContent
        })
    }

  def getPowerStations = AuthenticatedAction(userService).async(parse.empty){ request =>
    powerStationDao.getPowerStations(request.user.id)
      .map(powerStations => Ok(Json.toJson(powerStations)(Writes.seq(PowerStation.powerStationWrites))))
  }
}
