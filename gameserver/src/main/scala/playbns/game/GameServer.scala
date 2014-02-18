/*
 * This file is part of PlayBnS
 *                      <https://github.com/HeXLaB/play.bns>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2013-2014
 *               HeXLaB Team
 *                           All rights reserved
 */

package playbns.game

import akka.actor.{Props, ActorSystem}
import hexlab.morf.config.ConfigUtil
import hexlab.morf.executor.MessageExecutor.{RegisterHandler, RegisterData, RegisterConfig}
import hexlab.morf.util.Log
import java.io.File
import playbns.common.{ActorNameFactory, HandlerUtil}
import playbns.game.config.{MainConfig, ConfigMarker}
import playbns.game.handlers.HandlerMarker
import playbns.game.network.NetworkServer

/**
 * This class is an GameServer entry point
 *
 * @author hex1r0
 */
object GameServer {
  private val _log = Log[GameServer.type]

  val configRoot = "config"
  val systemName = "MORF-BNS-GameServer"

  def main(args: Array[String]) {
    installConfig(args)
    installDatabase(args)

    // load configuration classes
    val configs = ConfigUtil.loadAllFrom(configRoot, GameServer.getClass, classOf[ConfigMarker].getPackage.getName)

    // try to resolve main config
    val mainConfig = configs.collectFirst { case x: MainConfig => x }.get

    val port = mainConfig.CLIENT_PORT

    // load datapack
    // TODO
    val datapack = List().toStream

    // load handlers
    val handlerClasses = HandlerUtil.loadAllFrom(GameServer.getClass, classOf[HandlerMarker].getPackage.getName)

    // create system
    val system = ActorSystem.create(systemName)

    // create supervisor
    val supervisor = system.actorOf(Props(classOf[GameServerSupervisor]), ActorNameFactory.makeSupervisorName[GameServerSupervisor](port))

    // register everything
    configs foreach (config => supervisor ! RegisterConfig(config))
    datapack foreach (data => supervisor ! RegisterData(data))
    handlerClasses foreach { case (annot, clazz) => supervisor ! RegisterHandler(annot, clazz) }

    // create network
    system.actorOf(Props(classOf[NetworkServer], port), ActorNameFactory.makeNetworkServerName[NetworkServer](port))
  }

  def installConfig(args: Array[String]) {
    val configDir = new File(configRoot)
    if ((args exists (_ == "--install-config")) || !configDir.exists()) {
      _log.info("Configs:  Installing")
      configDir.mkdir()
      ConfigUtil.createAllFrom(configRoot, GameServer.getClass, classOf[ConfigMarker].getPackage.getName)
      _log.info("Configs: Installed")
    }
  }

  def installDatabase(args: Array[String]) {
    if (args exists (_ == "--install-db")) {
      _log.info("Database: Installing")
      // TODO
      _log.info("Database: Installed")
    }

  }
}
