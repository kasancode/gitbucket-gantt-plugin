package io.github.gitbucket.ganttchart.controller

import gantt.gitbucket.html
import gitbucket.core.controller.ControllerBase
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.service._
import gitbucket.core.util.ReferrerAuthenticator
import java.util.{Calendar, Date}


import gitbucket.core.util.Implicits._
import gitbucket.core.model.IssueComment

import io.github.gitbucket.ganttchart.service.GanttService

class GanttChartController extends GanttChartControllerBase
  with RepositoryService
  with AccountService
  with ReferrerAuthenticator
  with IssuesService
  with RequestCache
  with GanttService

trait GanttChartControllerBase extends ControllerBase {

  self: RepositoryService
    with AccountService
    with ReferrerAuthenticator
    with IssuesService
    with RequestCache
    with GanttService =>


  get("/:owner/:repository/gantt/:span")(
    referrersOnly {
      repository: RepositoryInfo => {
        var span = params("span").toIntOpt.getOrElse(7)
        if (span < 1) span = 7
        val fSpan = span / 3;

        val toDate = addDays(new Date(), fSpan)
        val fromDate = addDays(toDate, -span - fSpan)
        val comments = getCommentsByDate(repository.owner, repository.name, fromDate, toDate)
        val issues = getIssuesByComments(repository.owner, repository.name, fromDate, toDate, comments)
        val milestones = getMilestonesByIssues(repository.owner, repository.name, issues)

        html.gantt(
          repository,
          milestones,
          issues,
          comments,
          fromDate,
          toDate,
          span
        )
      }
    }
  )
}
