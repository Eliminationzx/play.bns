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

package playbns.game.network.message

import playbns.common.network.{BnSClientMessage, EmptyClientMessage}

/**
 * This class ...
 *
 * @author hex1r0
 */
object cm {
  class Ping extends EmptyClientMessage

  class Unknown_0011 extends BnSClientMessage {
    def readImpl() {
      // 010064d067000000000079fc340000000000
      // 1c001462070000000000dbf3160000000000
    }
  }

  class Unknown_0019 extends BnSClientMessage {
    def readImpl() {
      // 0000000000
    }
  }

  class Unknown_0024 extends BnSClientMessage {
    def readImpl() {
      // 8b30a5000000000000000000
    }
  }

  class Unknown_0147 extends EmptyClientMessage

  class Unknown_0219 extends BnSClientMessage {
    def readImpl() {
      // 0000000000
    }
  }

  class Unknown_0222 extends BnSClientMessage {
    def readImpl() {
      // 0100000000
    }
  }
}
