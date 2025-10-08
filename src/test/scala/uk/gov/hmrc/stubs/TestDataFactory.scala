/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.stubs

import uk.gov.hmrc.stubs.enums.ContactTypeOrder.First
import uk.gov.hmrc.stubs.models.{AccountingPeriod, BusinessEntity, Company, Contact, SeniorAccountingOfficer, Notification, PastSeniorAccountingOfficer, Submission}

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

object TestDataFactory {

  private object Defaults {
    val companyName  = "Valid Test Company Ltd"
    val crn          = "12345678"
    val utr          = "1234567890"
    val contactName  = "Jamaica John"
    val contactRole  = "Officer"
    val contactPhone = "+44 20 1234 5678"
    val testDomain   = "testCompany.com"
    val qualified    = true
    val companyType = "LTD"
    val saoName      = "Jacob Jacobson"
  }

  def validBusinessEntity(
    id: UUID = UUID.randomUUID(),
    name: String = Defaults.companyName,
    crn: String = Defaults.crn,
    utr: String = Defaults.utr
  ): BusinessEntity = BusinessEntity(
    id = id,
    crn = crn,
    utr = utr,
    name = name,
    contacts = List(validContact()),
    submissions = Some(List(validSubmission())),
    createdAt = Instant.now()
  )

  def invalidBusinessEntity(): BusinessEntity = validBusinessEntity().copy(
    crn = "",
    utr = "",
    name = "",
    contacts = List.empty,
    submissions = None
  )

  def duplicateBusinessEntity(): BusinessEntity =
    validBusinessEntity().copy(name = "DuplicateCompany")

  def validNotification(): Notification = Notification(
    seniorAccountingOfficer = validSeniorAccountingOfficer(),
    companies = List(validCompany())
  )

  def validCompany(
    name:String = Defaults.companyName,
    crn: String = Defaults.crn,
    utr: String = Defaults.utr,
    companyType: String = Defaults.companyType,
    fye: Instant = oneYearAgo,
    qualified: Boolean = Defaults.qualified,
  ): Company = Company(
    companyName = name,
    companyRegistrationNumber = crn,
    uniqueTaxpayerReference = Some(utr),
    companyType = companyType,
    financialYearEnd = fye.plus(200, ChronoUnit.DAYS),
    pastSeniorAccountingOfficers = Some(List(validPastSeniorAccountingOfficer())),
    qualified = qualified,
    comments = Some("Test Company Comment")
  )

  def validContact(
    name: String = Defaults.contactName,
    role: String = Defaults.contactRole
  ): Contact = Contact(
    name = name,
    role = role,
    email = emailFor(name),
    phone = Defaults.contactPhone,
    order = First
  )

  def validSeniorAccountingOfficer(
    fullName: String = Defaults.contactName
  ): SeniorAccountingOfficer = SeniorAccountingOfficer(
    fullName = fullName,
    email = emailFor(fullName),
    accountingPeriod = validSeniorAccountingOfficerPeriod()
  )

  def validPastSeniorAccountingOfficer(
    fullName: String = Defaults.saoName
  ): PastSeniorAccountingOfficer = PastSeniorAccountingOfficer(
    fullName = fullName,
    accountingPeriod = validPastSeniorAccountingOfficerPeriod()
  )

  def validSubmission(): Submission = Submission(
    accountingPeriod = Some(validAccountingPeriod()),
    receivedDate = Instant.now()
  )

  def validAccountingPeriod(): AccountingPeriod = {
    val start = oneYearAgo
    AccountingPeriod(
      startDate = start,
      endDate = start.plus(365, ChronoUnit.DAYS),
      dueDate = Some(start.plus(395, ChronoUnit.DAYS))
    )
  }

  def validSeniorAccountingOfficerPeriod(): AccountingPeriod = {
    val start = oneYearAgo
    AccountingPeriod(
      startDate = start.plus(120, ChronoUnit.DAYS),
      endDate = start.plus(365, ChronoUnit.DAYS)
    )
  }

  def validPastSeniorAccountingOfficerPeriod(): AccountingPeriod = {
    val start = oneYearAgo
    AccountingPeriod(
      startDate = start,
      endDate = start.plus(119, ChronoUnit.DAYS)
    )
  }

  private def emailFor(name: String) = s"${name.toLowerCase.replace(" ", ".")}@${Defaults.testDomain}"

  private def oneYearAgo = Instant.now().minus(365, ChronoUnit.DAYS)
}
