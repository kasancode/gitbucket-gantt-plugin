package io.github.gitbucket.ganttchart.controller

import gantt.gitbucket.html
import gitbucket.core.controller.ControllerBase
import gitbucket.core.service
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.service.{AccountService, IssuesService, RepositoryService, RequestCache}
import gitbucket.core.util.ReferrerAuthenticator
import gitbucket.core.util.Implicits._

class GanttChartController extends GanttChartControllerBase
  with RepositoryService
  with AccountService
  with ReferrerAuthenticator
  with IssuesService
  with RequestCache

trait GanttChartControllerBase extends ControllerBase {
  self: RepositoryService
    with AccountService
    with ReferrerAuthenticator
    with IssuesService
    with RequestCache =>

  get("/:owner/:repository/gantt") (referrersOnly { repository:RepositoryInfo => {
    import IssuesService._

    val condition = service.IssuesService.IssueSearchCondition(request)

    val issues =       searchIssue(condition,
      false,
      0 ,
      100,
      (repository.owner, repository.name))

    for(i <- issues){
      i.issue.registeredDate

    }

    html.gantt(repository,
      searchIssue(condition,
        false,
        0 ,
        100,
        (repository.owner, repository.name)))
  }})
}
