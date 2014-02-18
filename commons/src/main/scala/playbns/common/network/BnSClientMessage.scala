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

package playbns.common.network

import akka.actor.ActorRef
import akka.util.ByteString
import hexlab.morf.network.ClientMessage
import playbns.common.model.{User, Account, Character}
import playbns.common.util.BnSBinaryReader

/**
 * This class ...
 *
 * @author hex1r0
 */
trait BnSClientMessage extends ClientMessage with BnSBinaryReader {
  var clientRef: ActorRef = _
  var user: User = _

  def account = user.activeAccount

  def account_=(account: Account) = user.activeAccount = account

  def character = user.activeChar

  def character_=(char: Character) = user.activeChar = char

  def send(messageId: Int, bytes: ByteString) = clientRef !(messageId, bytes)

  def send(m: BnSServerMessage) = clientRef ! m

  def send(m: BnSServerMessage*) = m foreach (clientRef ! _)
}