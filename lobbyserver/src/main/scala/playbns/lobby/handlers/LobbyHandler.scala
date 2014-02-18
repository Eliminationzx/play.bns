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

package playbns.lobby.handlers

import akka.util.ByteString
import hexlab.morf.executor.MessageHandler
import hexlab.morf.util.ByteArray
import java.net.InetAddress
import playbns.common.scope.LobbyExecutor
import playbns.lobby.network.message.{common, sm, cm}

/**
 * This class ...
 *
 * @author hex1r0
 */
@LobbyExecutor
class LobbyHandler extends MessageHandler {

  def init() {
    bind[cm.Connect](onConnect)
    bind[cm.Ping](onPing)
    bind[cm.EnterGame](onEnterGame)
    bind[cm.ServerList](onServerList)
    bind[cm.CharacterList](onCharacterList)
    bind[cm.CreateCharacter](onCreateCharacter)
    bind[cm.Unknown_0035](onUnknown_0035)
    bind[cm.Unknown_003e](onUnknown_003e)
    bind[cm.Unknown_0044](onUnknown_0044)
  }

  def onConnect(m: cm.Connect) {
    m send new sm.Connect(0x67D064, ByteArray("9F8E9A1F01B676C989D14C1582905955"))
  }

  def onPing(m: cm.Ping) {}

  def onEnterGame(m: cm.EnterGame) {
    m send(0x000e, ByteString(ByteArray("79fc340000000000")))
  }

  def onServerList(m: cm.ServerList) {
    m send new sm.ServerList(common.GameServerInfo(1, "play.bns", InetAddress.getByName("127.0.0.1"), 10100))
  }

  def onCharacterList(m: cm.CharacterList) {
    val charInfo = new common.CharacterInfo(
      slotUUID = ByteArray("E8E061402A9411E2A6FEE41F136C9588"),
      serverId = 1,
      charName = "hex1r0",
      race = 1,
      gender = 2,
      occupation = 3,
      appearance = ByteArray("01000A08020C2108100821030101030704090100320000000000056439000A1313BA2D46D3E7E22814E714CEE705F12305E7D3D8CEEC0A05E7DDD3A6F114FB0000F1CE00EC0000F1F1E200D800C400CE00E200CE00F10000D89C0000"))

    m send new sm.CharacterList(charInfo)
  }

  def onCreateCharacter(m: cm.CreateCharacter) {
    m send new sm.CreateCharacter(m.charInfo)
  }

  def onUnknown_0035(m: cm.Unknown_0035) {
    m send(0x0036, ByteString(ByteArray("00000000")))
  }

  def onUnknown_003e(m: cm.Unknown_003e) {
    m send(0x003f, ByteString(ByteArray("0700000001640000000000")))
  }

  def onUnknown_0044(m: cm.Unknown_0044) {
    m send(0x0045, ByteString(ByteArray("020000000000")))
  }
}
