package uk.gov.hmrc.stubs

import uk.gov.hmrc.stubs.models.{AccountingPeriod, BusinessEntity, Contact, Submission}
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

object TestDataFactory {

  private object Defaults {
    val companyName = "Valid Test Company Ltd"
    val crn = "12345678"
    val utr = "1234567890"
    val contactName = "Jamaica John"
    val contactRole = "Officer"
    val contactPhone = "+44 20 1234 5678"
    val testDomain = "testcompany.com"
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
    createdAt = now()
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

  def validContact(
    name: String = Defaults.contactName,
    role: String = Defaults.contactRole
  ): Contact = Contact(
    name = name,
    role = role,
    email = emailFor(name),
    phone = Defaults.contactPhone,
    order = "1"
  )

  def validSubmission(): Submission = Submission(
    accountingPeriod = Some(validAccountingPeriod()),
    receivedDate = now()
  )

  def validAccountingPeriod(): AccountingPeriod = {
    val start = oneYearAgo
    AccountingPeriod(
      startDate = start,
      endDate = start.plus(365, ChronoUnit.DAYS),
      dueDate = start.plus(395, ChronoUnit.DAYS)
    )
  }

  private def now(): Instant = Instant.now().truncatedTo(ChronoUnit.MINUTES)

  private def emailFor(name: String) = s"${name.toLowerCase.replace(" ", ".")}@${Defaults.testDomain}"

  private def oneYearAgo = Instant.now().minus(365, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MINUTES)
}
