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

package playbns.auth.network

import hexlab.morf.util.Erasure
import scala.collection.mutable
import scala.reflect.ClassTag
import playbns.common.network.BnSClientMessage

/**
 * This class ...
 *
 * @author hex1r0
 */
object registry {
  val cms = new mutable.HashMap[String, Class[_]]

  // -- client messages

  bind[cm.StsConnect]("/Sts/Connect")
  bind[cm.StsPing]("/Sts/Ping")
  bind[cm.AuthKeyData]("/Auth/KeyData")
  bind[cm.AuthLoginStart]("/Auth/LoginStart")
  bind[cm.AuthLoginFinish]("/Auth/LoginFinish")
  bind[cm.AuthLogoutMyClient]("/Auth/LogoutMyClient")
  bind[cm.AuthRequestToken]("/Auth/RequestToken")
  bind[cm.AuthGetMyUserInfo]("/Auth/GetMyUserInfo")
  bind[cm.AuthRequestGameToken]("/Auth/RequestGameToken")
  bind[cm.GameAccountListMyAccounts]("/GameAccount/ListMyAccounts")
  bind[cm.PresenceUserInfo]("/Presence/UserInfo")
  bind[cm.SecondPasswordGetStatus]("/SecondPassword/GetStatus")
  bind[cm.SlotListSlots]("/Slot/ListSlots")

  def bind[T: ClassTag](messageId: String) = {
    cms += messageId -> Erasure[T]
  }

  def findCM(messageId: String): Option[BnSClientMessage] = {
    cms get messageId map (_.newInstance().asInstanceOf[BnSClientMessage])
  }
}
