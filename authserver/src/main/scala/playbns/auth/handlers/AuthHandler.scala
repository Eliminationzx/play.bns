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

package playbns.auth.handlers

import hexlab.morf.executor.MessageHandler
import playbns.auth.network.message.cm
import playbns.common.scope.AuthExecutor

/**
 * This class ...
 *
 * @author hex1r0
 */
@AuthExecutor
class AuthHandler extends MessageHandler {

  def init() {
    bind[cm.AuthLoginStart](onAuthLoginStart)
    bind[cm.AuthKeyData](onAuthKeyData)
    bind[cm.AuthLoginFinish](onAuthLoginFinish)
    bind[cm.AuthRequestToken](onAuthRequestToken)
    bind[cm.GameAccountListMyAccounts](onGameAccountListMyAccounts)
    bind[cm.AuthGetMyUserInfo](onAuthGetMyUserInfo)
    bind[cm.AuthRequestGameToken](onAuthRequestGameToken)
    bind[cm.SecondPasswordGetStatus](onSecondPasswordGetStatus)
    bind[cm.SlotListSlots](onSlotListSlots)
  }

  def onAuthLoginStart(m: cm.AuthLoginStart) {

  }

  def onAuthKeyData(m: cm.AuthKeyData) {

  }

  def onAuthLoginFinish(m: cm.AuthLoginFinish) {

  }

  def onAuthRequestToken(m: cm.AuthRequestToken) {

  }

  def onGameAccountListMyAccounts(m: cm.GameAccountListMyAccounts) {

  }

  def onAuthGetMyUserInfo(m: cm.AuthGetMyUserInfo) {

  }

  def onAuthRequestGameToken(m: cm.AuthRequestGameToken) {

  }

  def onSecondPasswordGetStatus(m: cm.SecondPasswordGetStatus) {

  }

  def onSlotListSlots(m: cm.SlotListSlots) {

  }

}
