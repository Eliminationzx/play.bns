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

package bns.lobby.network.message

import bns.common.network.{EmptyClientMessage, BnSClientMessage}
import bns.lobby.network.message.common.CharacterInfo
import hexlab.morf.util.ByteArray
import hexlab.morf.util.ByteArray.ByteArray

/**
 * This class ...
 *
 * @author hex1r0
 */
object cm {

  class Connect extends BnSClientMessage {
    var authToken = new ByteArray.ByteArray(16)
    var userId = new ByteArray.ByteArray(16)

    def readImpl() {
      readBytes(authToken)
      readBytes(userId)
    }
  }

  class Ping extends EmptyClientMessage

  class EnterGame extends BnSClientMessage {
    var slotUUID: ByteArray = _

    def readImpl() {
      slotUUID = readBytes(16)
    }
  }

  class ServerList extends EmptyClientMessage

  class CharacterList extends EmptyClientMessage

  class CreateCharacter extends BnSClientMessage {
    var charInfo = new CharacterInfo()

    def readImpl() {
      charInfo.slotUUID = readBytes(16)
      charInfo.serverId = readH
      charInfo.charName = readUtf16

      skip(2) // readH // serializable block header (length)
      skip(2) // readH // serializable block header (length)
      readAscii // serializable format (cccNcs)

      skip(2) // readH // index
      charInfo.race = readC // c

      skip(2) // readH // index
      charInfo.gender = readC // c

      skip(2) // readH // index
      charInfo.occupation = readC // c

      skip(2) // readH // index
      val len = readH // N
      charInfo.appearance = read(len) // appearance

      // we do not need following data
      //readH // index
      //readNullTerminatedString
    }
  }

  class Unknown_0035 extends EmptyClientMessage

  class Unknown_003e extends BnSClientMessage {
    def readImpl() {
      // 010000000000000000
    }
  }

  class Unknown_0044 extends EmptyClientMessage

}
