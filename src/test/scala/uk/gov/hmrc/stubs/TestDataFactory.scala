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
import com.github.javafaker.Faker
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import scala.util.Random

object TestDataFactory {

  private val faker = new Faker(new java.util.Locale("en-GB"))

  def validBusinessEntity(
    id: UUID = UUID.randomUUID(),
    companyName: String = faker.company.name(),
    crn: String = randomAlphanumericId(10),
    utr: Option[String] = Some(randomAlphanumericId(8))
  ): BusinessEntity = BusinessEntity(
    id = id,
    crn = crn,
    utr = utr,
    name = companyName,
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
    validBusinessEntity(companyName = "DuplicateCompany")

  def validNotification(): Notification = Notification(
    seniorAccountingOfficer = validSeniorAccountingOfficer(),
    companies = List(validCompanyNoQualifications())
  )

  def validContact(
    name: String = faker.name().fullName(),
    role: String = faker.job().position()
  ): Contact = Contact(
    name = name,
    role = role,
    email = emailFor(name),
    phone = faker.phoneNumber().phoneNumber(),
    order = First
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
    id: Option[String] = None,
    submitter: String = faker.name().fullName(),
    authorisedSao: String = faker.name.fullName()
  ): Certificate = Certificate(
    certificateId = id,
    submissionBy = submitter,
    authorisingSeniorAccountingOfficer = authorisedSao,
    companies = List(validCompanyNoQualifications(), validCompanyWithQualifications())
  )

  def validCompanyNoQualifications(
    name: String = faker.company.name(),
    crn: String = randomAlphanumericId(10),
    utr: Option[String] = Some(randomAlphanumericId(8)),
    companyModel: String = "LTD",
    isQualified: Boolean = false,
    regimes: List[TaxRegime] = List(TaxRegime()),
    notes: Option[String] = Some(Faker.instance().lorem().paragraph(3))
  ): Company = Company(
    companyName = name,
    companyRegistrationNumber = crn,
    uniqueTaxpayerReference = utr,
    companyType = companyModel,
    financialYearEnd = Instant.now().plus(30, ChronoUnit.DAYS),
    pastSeniorAccountingOfficers = Some(List(validPastSeniorAccountingOfficer())),
    qualified = isQualified,
    affectedTaxRegimes = regimes,
    comment = notes
  )

  def validCompanyWithQualifications(): Company =
    validCompanyNoQualifications(
      name = faker.company.name(),
      isQualified = true,
      regimes = List(TaxRegime(vat = true, corporationTax = true, stampDutyLandTax = true)),
      notes = Some(Faker.instance().lorem().paragraph(2))
    )

  def validSeniorAccountingOfficer(
    fullName: String = faker.name().fullName()
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
    fullName: String = faker.name().fullName()
  ): PastSeniorAccountingOfficer = PastSeniorAccountingOfficer(
    fullName = fullName,
    accountingPeriod = validAccountingPeriod().copy(
      startDate = oneYearAgo,
      endDate = oneYearAgo.plus(119, ChronoUnit.DAYS),
      dueDate = None
    )
  )

  private def emailFor(name: String) = s"${name.toLowerCase.replace(" ", ".")}@${faker.internet().domainName()}"

  private def oneYearAgo = Instant.now().minus(365, ChronoUnit.DAYS)

  def randomAlphanumericId(length: Int): String = Random.alphanumeric.take(length).mkString
}
