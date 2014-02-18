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

import akka.actor.IO
import akka.actor.IO.Iteratee
import akka.actor.IO.SocketHandle
import akka.util.ByteString
import hexlab.morf.crypto.srp.SRPServer
import hexlab.morf.network.MMOClientConnection
import hexlab.morf.util.{HexUtil, RC4Cipher}
import java.util.UUID
import scala.Some
import playbns.auth.network.message.Sts

/**
 * This class is a temporary solution and will be reworked in the future
 *
 * @author hex1r0
 */
class AuthConnection(socket: SocketHandle) extends MMOClientConnection(socket) {
  val _srp = new SRPServer
  var _rc4: Option[RC4Cipher] = None

  // --
  var _userName: String = _
  var _fullUserName: String = _
  // --

  val _userId: String = UUID.randomUUID().toString

  override def onReadyRead(bytes: ByteString): ByteString = {
    val decoded = decode(bytes)

    logPacket("C -> S", decoded)

    decoded
  }

  override def onReadyWrite(bytes: ByteString): ByteString = {
    logPacket("S -> C", bytes)

    encode(bytes)
  }

  def decode(bytes: ByteString) = _rc4 match {
    case Some(c) => ByteString(c.decode(bytes.toArray))
    case _ => bytes
  }

  def encode(bytes: ByteString) = _rc4 match {
    case Some(c) => ByteString(c.encode(bytes.toArray))
    case _ => bytes
  }

  def logPacket(typ: String, bytes: ByteString) {
    val array = bytes.toArray
    val pref = "A [%s] ".format(typ)

    log.info(pref + HexUtil.toHexString(array))
    //log.info(HexUtil.toHexString(array))
  }

  def send(request: Sts.Request) {
    send(request.toByteString)
  }

  def send(response: Sts.Response) {
    send(response.toByteString)
  }

  def send(bytes: ByteString) {
    socket.write(onReadyWrite(bytes))
  }

  def parseCP: Iteratee[Unit] = {
    IO repeat {
      for {
        request <- Sts.RequestParser.parse
      } yield {
        onRequest(request)
      }
    }
  }

  def onRequest(request: Sts.Request) {
    //log.info(request.header.packetId)
    implicit val session = request.session


    request.header.packetId match {
      case Sts.PacketId.STS_CONNECT =>
      // ignore
      case Sts.PacketId.AUTH_LOGINSTART =>
        _fullUserName = Sts.AuthLoginStart.parse(request)
        //_fullUserName += "_fullUserName"
        _userName = _fullUserName.split("@")(0)
        sendAuthLoginStartResponse

      case Sts.PacketId.AUTH_KEYDATA =>
        val am = Sts.AuthKeyData.parse(request)
        sendAuthKeyDataResponse(am)

      case Sts.PacketId.AUTH_LOGINFINISH =>
        sendPresenceUserInfoRequest(_userId, _userName)
        sendAuthLoginFinishResponse(_userId, _userName)

      case Sts.PacketId.AUTH_REQUESTTOKEN =>
        sendAuthRequestTokenResponse

      case Sts.PacketId.GAMEACCOUNT_LISTMYACCOUNTS =>
        sendGameAccounListMyAccountsResponse(_userId)

      case Sts.PacketId.AUTH_GETMYUSERINFO =>
        sendAuthGetMyUserInfoResponse(_userId, _userName, _fullUserName)

      case Sts.PacketId.AUTH_REQUESTGAMETOKEN =>
        sendAuthRequestGameTokenResponse

      case Sts.PacketId.SECONDPASSWORD_GETSTATUS =>
        sendSecondPasswordGetStatusResponse

      case Sts.PacketId.SLOT_LISTSLOTS =>
        sendSlotListSlotsResponse

      case Sts.PacketId.AUTH_PING =>

      case _ =>
        log.warn("Unknown request=" + request)
    }
  }

  def sendAuthLoginStartResponse(implicit session: Int) {
    _srp.createVerifier(_fullUserName, "1")
    val salt = _srp.getSalt
    val b = _srp.getB
    send(Sts.AuthLoginStartResponse.create(salt, b, session))
  }

  def sendAuthKeyDataResponse(am: (Array[Byte], Array[Byte]))(implicit session: Int) {
    _srp.processCalculations(am._1)
    val m2 = _srp.getHAMK
    val skey = _srp.getSessionKey
    send(Sts.AuthKeyDataResponse.create(m2, session))

    _rc4 = Option(new RC4Cipher(skey))
  }

  def sendPresenceUserInfoRequest(userId: String, userName: String) {
    send(Sts.PresenceUserInfoRequest.create(userId = userId, userName = userName))
  }

  def sendAuthLoginFinishResponse(userId: String, userName: String)(implicit session: Int) {
    send(Sts.AuthLoginFinishResponse.create(userId = userId, userName = userName, locationId = "CFB69FCD-C339-42D4-8E26-E6313093E651", session = session))
  }

  def sendAuthRequestTokenResponse(implicit session: Int) {
    send(Sts.AuthRequestTokenResponse.create(authToken = "Qjg5NTFDOTEtQjJCNy1FMTExLTk1QUEtRTYxRjEzNUU5OTJGOjM2RjE1NTU2LUQ0QkQtNEYwQS05Mzg0LUNCQTc0QTE1NkM5Qg==", session = session))
  }

  def sendGameAccounListMyAccountsResponse(userId: String)(implicit session: Int) {
    send(Sts.GameAccounListMyAccountsResponse.create(userId = userId, session = session))
  }

  def sendAuthGetMyUserInfoResponse(userId: String, userName: String, fullUserName: String)(implicit session: Int) {
    send(
      Sts.AuthGetMyUserInfoResponse.create(
        userId = userId,
        userName = userName,
        fullUserName = fullUserName,
        session = session
      )
    )
  }

  def sendAuthRequestGameTokenResponse(implicit session: Int) {
    send(Sts.AuthRequestGameTokenResponse.create(token = "95166288-6515-42D9-9AF3-FF2A2AB40049", session = session))
  }

  def sendSecondPasswordGetStatusResponse(implicit session: Int) {
    send(Sts.SecondPasswordGetStatusResponse.create(session = session))
  }

  def sendSlotListSlotsResponse(implicit session: Int) {
    send(Sts.SlotListSlotsResponse.create(session = session))
  }
}
