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

package playbns.lobby.network.message

import hexlab.morf.util.Erasure
import playbns.common.network.{BnSServerMessage, BnSClientMessage}
import scala.collection.mutable
import scala.reflect.ClassTag

/**
 * This class ...
 *
 * @author hex1r0
 */
object registry {
  val cms = new mutable.HashMap[Int, Class[_]]
  val sms = new mutable.HashMap[Class[_], Int]

  // -- client messages

  bind[cm.Connect](0x0003)
  bind[cm.Ping](0x0009)
  bind[cm.EnterGame](0x000d)
  bind[cm.ServerList](0x0018)
  bind[cm.CharacterList](0x001b)
  bind[cm.CreateCharacter](0x0024)
  bind[cm.Unknown_0035](0x0035)
  bind[cm.Unknown_003e](0x003e)
  bind[cm.Unknown_0044](0x0044)

  // -- server messages

  bind[sm.Connect](0x0004)
  bind[sm.ServerList](0x0019)
  bind[sm.CharacterList](0x001c)
  bind[sm.CreateCharacter](0x0025)

  def bind[T: ClassTag](messageId: Int) = {
    Erasure[T] match {
      case x if x == classOf[BnSClientMessage] => cms += messageId -> x
      case x if x == classOf[BnSServerMessage] => sms += x -> messageId
    }
  }

  def findCM(opcode: Int): Option[BnSClientMessage] = {
    cms get opcode map (_.newInstance().asInstanceOf[BnSClientMessage])
  }

  def findSM(clazz: Class[_]): Option[Int] = sms get clazz
}
