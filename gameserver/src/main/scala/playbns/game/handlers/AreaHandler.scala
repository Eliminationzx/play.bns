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

package playbns.game.handlers

import playbns.common.scope.AreaExecutor
import playbns.game.network.cm
import hexlab.morf.executor.MessageHandler

/**
 * This class ...
 *
 * @author hex1r0
 */
@AreaExecutor
class AreaHandler(areaId: Int) extends MessageHandler {
  def init() {
    bind[cm.Ping](onPing)
    bind[cm.Unknown_0011](onUnknown_0011)
    bind[cm.Unknown_0019](onUnknown_0019)
    bind[cm.Unknown_0024](onUnknown_0024)
    bind[cm.Unknown_0147](onUnknown_0147)
    bind[cm.Unknown_0219](onUnknown_0219)
    bind[cm.Unknown_0222](onUnknown_0222)
  }

  def onPing(m: cm.Ping) {}

  def onUnknown_0011(m: cm.Unknown_0011) {

  }

  def onUnknown_0019(m: cm.Unknown_0019) {

  }

  def onUnknown_0024(m: cm.Unknown_0024) {

  }

  def onUnknown_0147(m: cm.Unknown_0147) {

  }

  def onUnknown_0219(m: cm.Unknown_0219) {

  }

  def onUnknown_0222(m: cm.Unknown_0222) {

  }
}
