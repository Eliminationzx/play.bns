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

package playbns.game.config

import hexlab.morf.config.{Config, ConfigProperty}
import java.net.InetAddress

/**
 * This class ...
 *
 * @author hex1r0
 */
@Config("main.conf")
class MainConfig {
  @ConfigProperty("authserver.network.client.host",
    "0.0.0.0",
    "Host that will be used to listen for client connections")
  var CLIENT_HOST: InetAddress = _

  @ConfigProperty("authserver.network.client.port",
    10100,
    "Port that will be used to listen for client connections")
  var CLIENT_PORT: Int = _

  @ConfigProperty("authserver.network.lobby.host",
    "0.0.0.0",
    "Host that will be used to resolve auth server")
  var AUTH_HOST: InetAddress = _

  @ConfigProperty("authserver.network.lobby.port", 9000,
    "Port that will be used to resolve auth server")
  var AUTH_PORT: Int = _

  @ConfigProperty("authserver.database.driver",
    "com.mysql.jdbc.Driver",
    "Database driver")
  var DATABASE_DRIVER: String = _

  @ConfigProperty("authserver.database.url",
    "jdbc:mysq://127.0.0.1:3306/playbns?useUnicode=true&characterEncoding=UTF-8",
    "Database connection URL")
  var DATABASE_URL: String = _

  @ConfigProperty("authserver.database.user", "root", "Database user")
  var DATABASE_USER: String = _

  @ConfigProperty("authserver.database.password", "1", "Database password")
  var DATABASE_PWD: String = _
}
