package io.github.gitbucket.ganttchart.service

import java.util.{Date,Calendar}
import gitbucket.core.model.{
  Issue,
  PullRequest,
  IssueComment,
  IssueLabel,
  Label,
  Account,
  Repository,
  CommitState,
  Role,
  Milestone
}
import gitbucket.core.model.Profile._
import gitbucket.core.model.Profile.profile.blockingApi._
import gitbucket.core.model.Profile.dateColumnType

trait GanttService {


  def getCommentsByDate(owner: String,  repository: String, fromDate: Date, toDate :Date)(implicit s: Session):List[IssueComment] = {
    IssueComments.filter(r =>
      (r.userName === owner) &&
        (r.repositoryName === repository) &&
        (r.registeredDate >= fromDate) &&
        (r.registeredDate <= toDate))
      .sortBy(
        r => r.registeredDate)
      .list
  }

  def getIssuesByComments(owner: String,  repository: String, fromDate:Date, toDate:Date, comments: List[IssueComment])(implicit s: Session):List[Issue] = {
    val ids = comments
      .map(r => r.issueId)
      .distinct

    Issues.filter(r =>
      r.userName === owner &&
      r.repositoryName === repository &&
      ((r.issueId inSetBind ids) || (
        r.registeredDate >= fromDate &&
        r.registeredDate <= toDate
      )))
      .sortBy(r => r.registeredDate)
      .list
  }

  def getMilestonesByIssues(owner: String,  repository: String, issues: List[Issue])(implicit s: Session): List[(Milestone,Int,Int)] = {
    val ids = issues
      .filter(r => r.milestoneId.isDefined)
      .map (r => r.milestoneId.getOrElse(-1))
      .distinct

    val counts = Issues
      .filter(r =>
        r.userName === owner &&
        r.repositoryName === repository &&
        (r.milestoneId inSetBind ids))
      .groupBy{r => r.milestoneId -> r.closed}
      .map { case (t1, t2) => t1._1 -> t1._2 -> t2.length }
      .list
      .toMap

    val milestones = Milestones
      .filter(r => r.milestoneId inSetBind ids)
      .sortBy(r => r.dueDate)
      .list


    milestones.map(r => (
      r,
      counts.getOrElse((r.milestoneId, false), 0),
      counts.getOrElse((r.milestoneId, true), 0)
    ))
  }

  def addDays(date: Date, days: Int) = {
    val cal = Calendar.getInstance
    cal.setTime(date)
    cal.add(Calendar.DATE, days)
    cal.getTime
  }
}
