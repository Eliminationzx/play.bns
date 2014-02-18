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

import hexlab.morf.network.ClientMessage

/**
 * This class ...
 *
 * @author hex1r0
 */
object cm {

  trait StsMessage extends ClientMessage {

  }


  class StsConnect extends StsMessage {
    def readImpl() {

    }
  }

  class StsPing extends StsMessage {
    def readImpl() {

    }
  }

  class AuthKeyData extends StsMessage {
    def readImpl() {

    }
  }

  class AuthLoginStart extends StsMessage {
    def readImpl() {

    }
  }

  class AuthLoginFinish extends StsMessage {
    def readImpl() {

    }
  }

  class AuthLogoutMyClient extends StsMessage {
    def readImpl() {

    }
  }

  class AuthRequestToken extends StsMessage {
    def readImpl() {

    }
  }

  class AuthGetMyUserInfo extends StsMessage {
    def readImpl() {

    }
  }

  class AuthRequestGameToken extends StsMessage {
    def readImpl() {

    }
  }

  class GameAccountListMyAccounts extends StsMessage {
    def readImpl() {

    }
  }

  class PresenceUserInfo extends StsMessage {
    def readImpl() {

    }
  }

  class SecondPasswordGetStatus extends StsMessage {
    def readImpl() {

    }
  }

  class SlotListSlots extends StsMessage {
    def readImpl() {

    }
  }

}
