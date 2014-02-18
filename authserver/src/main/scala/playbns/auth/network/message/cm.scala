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

package playbns.auth.network.message

import hexlab.morf.util.Base64
import hexlab.morf.util.ByteArray.ByteArray

/**
 * This class ...
 *
 * @author hex1r0
 */
object cm {

  class StsConnect extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

  class StsPing extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

  class AuthKeyData extends StsClientMessage {
    var a: ByteArray = _
    var m: ByteArray = _

    def readImpl() {
      val keyDataText = (body \\ "KeyData").text
      val keyData = Base64.decode(keyDataText)

      val reader = newBinaryReader(keyData)
      a = reader.readBytes(reader.readInt)
      m = reader.readBytes(reader.readInt)
    }
  }

  class AuthLoginStart extends StsClientMessage {
    var salt: ByteArray = _
    var b: ByteArray = _

    def readImpl() {
      val keyDataText = (body \\ "KeyData").text
      val keyData = Base64.decode(keyDataText)

      val reader = newBinaryReader(keyData)
      salt = reader.readBytes(reader.readInt)
      b = reader.readBytes(reader.readInt)
    }
  }

  class AuthLoginFinish extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

  class AuthLogoutMyClient extends StsClientMessage {
    def readImpl() {

    }
  }

  class AuthRequestToken extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

  class AuthGetMyUserInfo extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

  class AuthRequestGameToken extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

  class GameAccountListMyAccounts extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

  class PresenceUserInfo extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

  class SecondPasswordGetStatus extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

  class SlotListSlots extends StsClientMessage {
    def readImpl() {
      // ignore
    }
  }

}
