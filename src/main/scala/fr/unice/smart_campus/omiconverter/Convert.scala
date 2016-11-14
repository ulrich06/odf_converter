/*
 * ************************************************************************
 *                  Université de Nice Sophia-Antipolis (UNS) -
 *                  Centre National de la Recherche Scientifique (CNRS)
 *                  Copyright © 2016 UNS, CNRS
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 3 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 *
 *     Author: Cyril Cecchinel – Laboratoire I3S – cecchine@i3s.unice.fr
 * ***********************************************************************
 */

package fr.unice.smart_campus.omiconverter

import java.io.IOException

import fr.i3s.modalis.cosmic.organizational.{Catalog, Container, Sensor}
import fr.unice.smartcampus.SmartCampusOrganization
import play.api.libs.json.{JsObject, Json}

import scala.xml.Elem

/**
  * Created by Cyril Cecchinel - I3S Laboratory on 14/11/2016.
  */
object Convert {

  def apply(organization:Catalog) = convert(organization)

  def retrieveLastValue(sensorName: String) = {
    val URL = s"http://smartcampus.unice.fr/sensors/$sensorName/data/last"
    try {
      val source = Json.parse(scala.io.Source.fromURL(URL).mkString)
      val date = (source \ "values" \\ "date").head.as[String]
      val value = (source \ "values" \\ "value").head.as[String]
      (date, value)
    } catch {case ioe: IOException => ((System.currentTimeMillis() / 1000).toString, "NULL")}
  }

  def buildLastValue(sensorName:String) = {
    val lastValue = retrieveLastValue(sensorName)
      <Object>
        <id>{s"Measurement"}</id>
        <InfoItem name="LastValue"><value>{lastValue._2}</value></InfoItem>
        <InfoItem name="LastDate"><value>{lastValue._1}</value></InfoItem>
      </Object>
  }
  def convertSensors(sensor: Sensor) = {
    <Object>
      <id>{sensor.name}</id>
      <InfoItem name="GivenName"><value>{sensor.name}</value></InfoItem>
      <InfoItem name="Observes"><value>{sensor.observes.name}</value></InfoItem>
      {buildLastValue(sensor.name)}
    </Object>
  }

  def convertContainer(container: Container):Elem = {
    <Object>
    <id>{container.name}</id>
      <InfoItem name="GivenName"><value>{container.name}</value></InfoItem>
      <InfoItem name="ContainerType"><value>{container.cType}</value></InfoItem>
      {container.contains.collect{case x:Sensor => x}.map(convertSensors) ++
      container.contains.collect{case x:Container => x}.map(convertContainer)}
    </Object>
  }

  def convert(organization:Catalog) = {
      <omi:omiEnvelope xmlns:omi="omi.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="omi.xsd omi.xsd" version="1.0" ttl="-1">
        <omi:write msgformat="odf" targetType="device">
          <omi:msg xmlns="odf.xsd" xsi:schemaLocation="odf.xsd odf.xsd">
            <Objects>
             {convertContainer(organization.root)}
            </Objects>
          </omi:msg>
        </omi:write>
      </omi:omiEnvelope>
  }
}

object Run extends App{
  println(Convert(SmartCampusOrganization.catalog))
}