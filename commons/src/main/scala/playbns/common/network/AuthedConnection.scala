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

import akka.actor.IO
import akka.actor.IO.{Iteratee, SocketHandle}
import akka.util.ByteString
import hexlab.morf.network.MMOClientConnection
import hexlab.morf.util.ByteArray.ByteArray
import playbns.common.crypto.BnsAESCipher
import playbns.common.network.protocol.BnsBin
import scala.collection.mutable

/**
 * This class ...
 *
 * @author hex1r0
 */
class AuthedConnection(socket: SocketHandle, key: ByteArray, handshake: ByteArray) extends MMOClientConnection(socket) {

  val ciphers = new mutable.HashMap[Int, BnsAESCipher]
  var _onRequest: (BnsBin.Frame) => Unit = onHandshake

  def decode(frame: BnsBin.Frame) = ciphers.get(frame.header.cipherId) match {
    case Some(c) => ByteString(c.decode(frame.body.toArray))
    case _ => frame.body
  }

  def encode(cipherId: Int, bytes: ByteString) = ciphers.get(cipherId) match {
    case Some(c) => ByteString(c.encode(bytes.toArray))
    case _ => bytes
  }

  def parseCP: Iteratee[Unit] = {
    IO repeat {
      for {
        frame <- BnsBin.parse
      } yield {
        _onRequest(frame)
      }
    }
  }

  def onHandshake(frame: BnsBin.Frame) {
    // TODO
    _onRequest = onMessage
  }

  def onMessage(frame: BnsBin.Frame) {
    log.info(frame.toString)
  }
}
