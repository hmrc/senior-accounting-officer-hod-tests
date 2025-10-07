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
import uk.gov.hmrc.stubs.models.{AccountingPeriod, BusinessEntity, Certificate, Company, Contact, Notification, PastSeniorAccountingOfficer, SeniorAccountingOfficer, Submission, TaxRegime}

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.util.Random

object TestDataFactory {

  private object Defaults {
    val testCompanyOneName = "Mapple myPhones Ltd"
    val testCompanyTwoName = "Wiperosoft Ltd"
    val contactName        = "Jamaica John"
    val contactRole        = "Officer"
    val contactPhone       = "+44 20 7946 0958"
    val testDomain         = "testCompany.com"
    val saoFullName        = "Sir Counts A Lot"
  }

  def validBusinessEntity(
    id: UUID = UUID.randomUUID(),
    name: String = Defaults.testCompanyOneName,
    crn: String = randomAlphanumericId(10),
    utr: Option[String] = Some(randomAlphanumericId(8))
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
    utr = None,
    name = "",
    contacts = List.empty,
    submissions = None
  )

  def duplicateBusinessEntity(): BusinessEntity =
    validBusinessEntity(name = "DuplicateCompany")

  def validNotification(): Notification = Notification(
    seniorAccountingOfficer = validSeniorAccountingOfficer(),
    companies = List(validCompany())
  )

  def validCompany(
    name: String = Defaults.companyName,
    crn: String = Defaults.crn,
    utr: String = Defaults.utr,
    companyType: String = Defaults.companyType,
    fye: Instant = Instant.now().plus(200, ChronoUnit.DAYS),
    qualified: Boolean = Defaults.qualified
  ): Company = Company(
    companyName = name,
    companyRegistrationNumber = crn,
    uniqueTaxpayerReference = Some(utr),
    companyType = companyType,
    financialYearEnd = fye,
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
    accountingPeriod = validAccountingPeriod().copy(
      startDate = oneYearAgo.plus(120, ChronoUnit.DAYS),
      endDate = Instant.now(),
      dueDate = None
    )
  )

  def validPastSeniorAccountingOfficer(
    fullName: String = Defaults.saoName
  ): PastSeniorAccountingOfficer = PastSeniorAccountingOfficer(
    fullName = fullName,
    accountingPeriod = validAccountingPeriod().copy(
      startDate = oneYearAgo,
      endDate = oneYearAgo.plus(119, ChronoUnit.DAYS),
      dueDate = None
    )
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

  def validCertificate(
    submitter: String = Defaults.contactName,
    authorisedSao: String = Defaults.saoFullName
  ): Certificate = Certificate(
    submissionBy = submitter,
    authorisingSeniorAccountingOfficer = authorisedSao,
    companies = List(validCompanyNoQualifications(), validCompanyWithQualifications())
  )

  def validCompanyNoQualifications(
    name: String = Defaults.testCompanyOneName,
    crn: String = randomAlphanumericId(10),
    utr: Option[String] = Some(randomAlphanumericId(8)),
    companyModel: String = "LTD",
    isQualified: Boolean = false,
    regimes: List[TaxRegime] = List(TaxRegime()),
    notes: String = "Some random string thing!"
  ): Company = Company(
    companyName = name,
    companyRegistrationNumber = crn,
    uniqueTaxpayerReference = utr,
    companyType = companyModel,
    financialYearEnd = Instant.now().plus(30, ChronoUnit.DAYS),
    accountingPeriod = validAccountingPeriod().copy(dueDate = None),
    qualified = isQualified,
    affectedTaxRegimes = regimes,
    comment = notes
  )

  def validCompanyWithQualifications(): Company =
    validCompanyNoQualifications(
      name = Defaults.testCompanyTwoName,
      isQualified = true,
      regimes = List(TaxRegime(vat = true, corporationTax = true, stampDutyLandTax = true)),
      notes = "Wiprosoft has problems man!"
    )

  private def emailFor(name: String) = s"${name.toLowerCase.replace(" ", ".")}@${Defaults.testDomain}"

  private def oneYearAgo = Instant.now().minus(365, ChronoUnit.DAYS)

  def randomAlphanumericId(length: Int): String = Random.alphanumeric.take(length).mkString
}
