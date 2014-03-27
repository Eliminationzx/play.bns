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

package playbns.game

import akka.actor.{ActorRef, Actor}
import hexlab.morf.core.Supervisor
import hexlab.morf.executor.MessageExecutor.CreateHandler
import hexlab.morf.util.ClassUtil.ClassExt
import playbns.common.scope.{LobbyExecutor, AreaExecutor}
import playbns.game.GameServerSupervisor.AreaSupervisor
import playbns.game.handlers.AreaHandler
import scala.collection.mutable

/**
 * This class ...
 *
 * @author hex1r0
 */
class GameServerSupervisor extends Supervisor with AreaSupervisor {
  override def receive: Actor.Receive = {
    super[Supervisor].receive orElse
      super[AreaSupervisor].receive
  }
}

object GameServerSupervisor {
  private[GameServerSupervisor] trait AreaSupervisor extends Supervisor {

    val areaExecutors = new mutable.HashMap[Int, ActorRef]

    override def receive: Actor.Receive = {
      case GetAreaExecutor(areaId) =>
        val ref = areaExecutors.getOrElseUpdate(areaId, newAreaExecutor(areaId))
        sender ! ref
    }

    def newAreaExecutor(areaId: Int) = {
      val executor = actorOf(name[AreaExecutor] + "-" + areaId)

      handlersOf[AreaExecutor] foreach (clazz => {
        val params: Seq[Any] = clazz match {
          case x if x.isChildOf[AreaHandler] => Seq(areaId)
          case _ => Seq()
        }

        executor ! CreateHandler(clazz, params)
      })

      save(executor)
    }

  }
}

case class GetAreaExecutor(areaId: Int)
