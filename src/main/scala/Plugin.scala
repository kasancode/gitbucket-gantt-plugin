import io.github.gitbucket.solidbase.model.Version
import gitbucket.core.controller.{Context, ControllerBase}
import gitbucket.core.plugin.{Link, PluginRegistry}
import gitbucket.core.service.RepositoryService.RepositoryInfo
import gitbucket.core.service.SystemSettingsService.SystemSettings
import io.github.gitbucket.ganttchart.controller.GanttChartController

class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "ganttchart"
  override val pluginName: String = "Gantt Chart Plugin"
  override val description: String = "GitBucket plug-in"
  override val versions: List[Version] = List(new Version("1.0.0"))


  override val controllers = Seq(
    "/*" -> new GanttChartController()
  )

  override val assetsMappings = Seq("/gantt" -> "/plugins/gantt/assets")

  override val repositoryMenus = Seq(
    (repositoryInfo: RepositoryInfo, context: Context) => Some(Link("gantt", "Gantt Chart", "/gantt/7", Some("tasklist")))
  )
}
