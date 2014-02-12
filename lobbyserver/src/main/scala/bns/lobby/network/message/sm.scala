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

import bns.common.network.BnSServerMessage
import hexlab.morf.util.ByteArray.ByteArray

/**
 * This class ...
 *
 * @author hex1r0
 */
object sm {

  class Connect(lobbyToken: Int, key: ByteArray) extends BnSServerMessage {
    def writeImpl() {
      writeD(lobbyToken)

      fill(0, 29)

      writeH(key.length)
      write(key)
    }
  }

  class ServerList(gsis: common.GameServerInfo*) extends BnSServerMessage {
    def writeImpl() {
      val blocks = gsis map {
        info =>
          val b = newBlock

          b.writeH(info.id) // social id
          b.writeH(info.id) // game id
          b.writeUtf16(info.name)
          b.writeC(1)
          b.writeC(1)
          b.write(info.ip.getAddress) // social
          b.writeH(info.port)
          b.writeD(0)
          b.writeC(1)
          b.writeC(1)
          b.writeC(1)
          b.writeH(0)
          b.writeC(0)
          b.writeC(1) // or 2
          b.write(info.ip.getAddress) // game
          b.writeH(info.port)
          b.writeH(0)
      }

      var size = (blocks map (_.length)).sum
      size += 2 // server count size

      writeD(size) // body size
      writeH(blocks.length) // server count
      blocks foreach write
    }
  }

  class CharacterList(cis: common.CharacterInfo*) extends BnSServerMessage {
    def writeImpl() {
      var size = (cis map (_.length)).sum
      size += 2 // character count size

      writeD(size) // body size
      writeH(cis.length) // server count
      cis foreach write
    }
  }

  class CreateCharacter(charInfo: common.CharacterInfo) extends BnSServerMessage {
    def writeImpl() {
      write(charInfo)
    }
  }

}
