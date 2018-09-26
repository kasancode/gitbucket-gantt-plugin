package io.github.gitbucket.ganttchart.view

import java.text.SimpleDateFormat
import java.util.{Date, Locale, TimeZone}

import com.nimbusds.jose.util.JSONObjectUtils
import gitbucket.core.controller.Context
import gitbucket.core.model.{Comment, CommitState, Issue, IssueComment}
import gitbucket.core.plugin.{PluginRegistry, RenderRequest}
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.service.{RepositoryService, RequestCache}
import gitbucket.core.util.{FileUtil, JGitUtil, StringUtil}
import gitbucket.core.view.helpers.createIssueLink
import gitbucket.core.view.{AvatarImageProvider, LinkConverter, helpers}
import play.twirl.api.{Html, HtmlFormat}
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


object ganttHelpers extends AvatarImageProvider with LinkConverter with RequestCache {

  def createMilestoneUrl(repository: RepositoryService.RepositoryInfo, milestone: String)(
    implicit context: Context
  ): String = {
    val userName = repository.repository.userName
    val repositoryName = repository.repository.repositoryName

    s"""${context.path}/${userName}/${repositoryName}/issues?milestone=${URLEncoder.encode(milestone, StandardCharsets.UTF_8.toString)}&state=open"""
  }

  def createIssueUrl(repository: RepositoryService.RepositoryInfo, issue: Issue)(
    implicit context: Context
  ): String = {
    val userName = repository.repository.userName
    val repositoryName = repository.repository.repositoryName

    if (issue.isPullRequest) {
      s"""${context.path}/${userName}/${repositoryName}/pull/${issue.issueId}"""
    }
    else {
      s"""${context.path}/${userName}/${repositoryName}/issues/${issue.issueId}"""
    }
  }

  def createCommitUrl(repository: RepositoryService.RepositoryInfo, commit: String)(
    implicit context: Context
  ): String = {
    val userName = repository.repository.userName
    val repositoryName = repository.repository.repositoryName

    s"""${context.path}/${userName}/${repositoryName}/commit/${commit}"""
  }

  def createCommentUrl(repository: RepositoryService.RepositoryInfo, issue: Issue, comment: IssueComment)(
    implicit context: Context
  ): String = {
    val userName = repository.repository.userName
    val repositoryName = repository.repository.repositoryName

    comment.action match {
      case "commit" =>
        val id = " ([a-f0-9]{40})$".r.findFirstMatchIn(comment.content)
          .map(m => m.group(1))
          .getOrElse("")

        s"""${context.path}/${userName}/${repositoryName}/commit/${id}"""

      case _ if issue.isPullRequest =>
        s"""${context.path}/${userName}/${repositoryName}/pull/${issue.issueId}#comment-${comment.commentId}"""

      case _ =>
        s"""${context.path}/${userName}/${repositoryName}/issues/${issue.issueId}#comment-${comment.commentId}"""
    }
  }

  def createGanttUrl(repository: RepositoryService.RepositoryInfo, span: Int)(
    implicit context: Context
  ): String = {
    val userName = repository.repository.userName
    val repositoryName = repository.repository.repositoryName

    s"""${context.path}/${userName}/${repositoryName}/gantt/${math.max(span, 1)}"""
  }
}
