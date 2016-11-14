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

import fr.i3s.modalis.cosmic.organizational.{Catalog, Container, Sensor}
import fr.unice.smartcampus.SmartCampusOrganization

/**
  * Created by Cyril Cecchinel - I3S Laboratory on 14/11/2016.
  */
object Convert {

  def apply(organization:Catalog) = convert(organization)

  def convertSensors(sensor: Sensor) = {
    <Object>
      <id>{sensor.name}</id>
      <InfoItem name="GivenName"><value>{sensor.name}</value></InfoItem>
      <InfoItem name="Observes"><value>{sensor.observes.name}</value></InfoItem>
    </Object>
  }


  def convert(organization:Catalog) = {
      <omi:omiEnvelope xmlns:omi="omi.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="omi.xsd omi.xsd" version="1.0" ttl="-1">
        <omi:write msgformat="odf" targetType="device">
          <omi:msg xmlns="odf.xsd" xsi:schemaLocation="odf.xsd odf.xsd">
            <Objects>
              {organization.getSensors(organization.root.name).map(convertSensors)}
            </Objects>
          </omi:msg>
        </omi:write>
      </omi:omiEnvelope>
  }
}

object Run extends App{
  println(Convert(SmartCampusOrganization.catalog))
}