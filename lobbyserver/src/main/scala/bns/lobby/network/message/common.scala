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

import bns.common.util.BnSBinaryWriter
import hexlab.morf.network.ServerMessage
import hexlab.morf.util.ByteArray._
import java.net.InetAddress

/**
 * This class ...
 *
 * @author hex1r0
 */
object common {

  case class GameServerInfo(id: Int, name: String, ip: InetAddress, port: Int)

  class CharacterInfo extends BnSBinaryWriter with ServerMessage {
    var slotUUID: ByteArray = _
    var serverId: Short = _
    var charName: String = _
    var race: Byte = _
    var gender: Byte = _
    var occupation: Byte = _
    var appearance: ByteArray = _

    def this(slotUUID: ByteArray,
             serverId: Short,
             charName: String,
             race: Byte,
             gender: Byte,
             occupation: Byte,
             appearance: ByteArray) {
      this()
      this.slotUUID = slotUUID
      this.serverId = serverId
      this.charName = charName
      this.race = race
      this.gender = gender
      this.occupation = occupation
      this.appearance = appearance

      writeImpl()
    }

    def writeImpl() {
      write(slotUUID)
      writeH(serverId)
      writeUtf16(charName)

      val b = newBlock

      b.writeAscii("cccNcssdwwwcddwdddddddd")

      b.writeH(0) // index
      b.writeC(race) // c

      b.writeH(1) // index
      b.writeC(gender) // c

      b.writeH(2) // index
      b.writeC(occupation) // c

      b.writeH(3) // index
      b.writeH(appearance.length) // N
      b.write(appearance)

      b.writeH(4) // index
      b.writeUtf16Nt(charName) // s

      b.writeH(5) // index
      b.writeUtf16Nt("") // s

      b.writeH(6) // index
      b.writeD(0) // d worldId 1101

      b.writeH(7) // index
      b.writeH(51194) // w

      b.writeH(8) // index
      b.writeH(51694) // w

      b.writeH(9) // index
      b.writeH(64751) // w

      b.writeH(10) // index
      b.writeC(10) // c level

      b.writeH(11) // index
      b.writeD(20091) // d

      b.writeH(12) // index
      b.writeD(442) // d HP

      b.writeH(13) // index
      b.writeH(0) // w

      b.writeH(14) // index
      b.writeD(1175) // d

      b.writeH(15) // index
      b.writeD(610151) // d hands

      b.writeH(16) // index
      b.writeD(0) // d

      b.writeH(17) // index
      b.writeD(910002) // d cloths

      b.writeH(18) // index
      b.writeD(0) // d maybe cloths part

      b.writeH(19) // index
      b.writeD(0) // d headwear

      b.writeH(20) // index
      b.writeD(0) // d headgear

      b.writeH(21) // index
      b.writeD(0) // d

      writeH(b.length + 2)
      writeH(b.length + 2)
      write(b)

      write("DD843452000000000100000000")
    }
  }

}
