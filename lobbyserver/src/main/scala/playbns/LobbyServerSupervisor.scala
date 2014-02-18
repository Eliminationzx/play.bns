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

package playbns

import akka.actor.{ActorRef, Actor}
import hexlab.morf.core.Supervisor
import hexlab.morf.executor.MessageExecutor.CreateHandler
import playbns.LobbyServerSupervisor.LobbySupervisor
import playbns.common.scope.LobbyExecutor

/**
 * This class ...
 *
 * @author hex1r0
 */
class LobbyServerSupervisor extends Supervisor with LobbySupervisor {
  override def receive: Actor.Receive = {
    super[Supervisor].receive orElse
      super[LobbySupervisor].receive
  }
}

object LobbyServerSupervisor {
  private[LobbyServerSupervisor] trait LobbySupervisor extends Supervisor {

    var lobbyExecutor: Option[ActorRef] = None

    override def receive: Actor.Receive = {
      case GetLobbyExecutor() =>
        if (lobbyExecutor.isEmpty)
          lobbyExecutor = Some(newLobbyExecutor)

        sender ! lobbyExecutor
    }

    def newLobbyExecutor = {
      val executor = actorOf(name[LobbyExecutor])

      handlersOf[LobbyExecutor] foreach (clazz => executor ! CreateHandler(clazz, Seq.empty))

      save(executor)
    }

  }
}

case class GetLobbyExecutor()
