import com.typesafe.sbt.packager.docker.DockerPermissionStrategy
import sbt.url
import sbtbuildinfo.BuildInfoPlugin

showCurrentGitBranch

git.useGitDescribe := true

lazy val commonSettings = Seq(
  organization := "org.hathitrust.htrc",
  organizationName := "HathiTrust Research Center",
  organizationHomepage := Some(url("https://www.hathitrust.org/htrc")),
  scalaVersion := "2.13.6",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:postfixOps",
    "-language:implicitConversions"
  ),
  resolvers ++= Seq(
    Resolver.mavenLocal,
    "HTRC Nexus Repository" at "https://nexus.htrc.illinois.edu/repository/maven-public"
  ),
  externalResolvers := Resolver.combineDefaultResolvers(resolvers.value.toVector, mavenCentral = false),
  Compile / packageBin / packageOptions += Package.ManifestAttributes(
    ("Git-Sha", git.gitHeadCommit.value.getOrElse("N/A")),
    ("Git-Branch", git.gitCurrentBranch.value),
    ("Git-Version", git.gitDescribedVersion.value.getOrElse("N/A")),
    ("Git-Dirty", git.gitUncommittedChanges.value.toString),
    ("Build-Date", new java.util.Date().toString)
  )
)

lazy val wartRemoverSettings = Seq(
  Compile / compile / wartremoverWarnings ++= Warts.unsafe.diff(Seq(
    Wart.DefaultArguments,
    Wart.NonUnitStatements,
    Wart.Any,
    Wart.StringPlusAny,
    Wart.OptionPartial
  ))
)

lazy val buildInfoSettings = Seq(
  buildInfoOptions ++= Seq(BuildInfoOption.BuildTime),
  buildInfoPackage := "utils",
  buildInfoKeys ++= Seq[BuildInfoKey](
    "gitSha" -> git.gitHeadCommit.value.getOrElse("N/A"),
    "gitBranch" -> git.gitCurrentBranch.value,
    "gitVersion" -> git.gitDescribedVersion.value.getOrElse("N/A"),
    "gitDirty" -> git.gitUncommittedChanges.value,
    "nameWithVersion" -> s"${name.value} ${version.value}"
  )
)

lazy val ammoniteSettings = Seq(
  libraryDependencies +=
    {
      val version = scalaBinaryVersion.value match {
        case "2.10" => "1.0.3"
        case _ ⇒  "2.4.0-23-76673f7f"
      }
      "com.lihaoyi" % "ammonite" % version % Test cross CrossVersion.full
    },
  Test / sourceGenerators += Def.task {
    val file = (Test / sourceManaged).value / "amm.scala"
    IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
    Seq(file)
  }.taskValue,
  connectInput := true,
  outputStrategy := Some(StdoutOutput)
)

lazy val dockerSettings = Seq(
  Docker / maintainer := "Boris Capitanu <capitanu@illinois.edu>",
  Docker / packageName := "services/ef-identifier-info",
  Docker / daemonUserUid := None,
  Docker / daemonUser := "daemon",
  dockerBaseImage := "anapsix/alpine-java:8",
  dockerExposedPorts := Seq(9000),
  dockerRepository := Some("docker.htrc.illinois.edu"),
  dockerPermissionStrategy := DockerPermissionStrategy.CopyChown,
  Universal / javaOptions ++= Seq("-Dpidfile.path=/dev/null")  // https://stackoverflow.com/questions/28351405/restarting-play-application-docker-container-results-in-this-application-is-alr
  //dockerUpdateLatest := true
)

lazy val `ef-identifier-info` = (project in file("."))
  .enablePlugins(PlayScala, BuildInfoPlugin, GitVersioning, GitBranchPrompt, JavaAppPackaging, DockerPlugin)
  .settings(commonSettings)
  .settings(dockerSettings)
  .settings(ammoniteSettings)
  .settings(
    name := "ef-identifier-info",
    libraryDependencies ++= Seq(
      guice,
      "org.hathitrust.htrc"           %% "data-model"               % "2.13",
      "org.apache.commons"            %  "commons-compress"         % "1.21",
      "com.atlassian.commonmark"      %  "commonmark"               % "0.17.0",
      "com.atlassian.commonmark"      %  "commonmark-ext-ins"       % "0.17.0",
      "com.atlassian.commonmark"      %  "commonmark-ext-heading-anchor" % "0.17.0",
      "com.atlassian.commonmark"      %  "commonmark-ext-gfm-strikethrough" % "0.17.0",
      "com.atlassian.commonmark"      %  "commonmark-ext-gfm-tables" % "0.17.0",
      "org.scalamock"                 %% "scalamock"                % "5.2.0"       % Test,
      "org.scalatestplus.play"        %% "scalatestplus-play"       % "5.1.0"       % Test
    ),
    developers := List(
      Developer(
        id = "capitanu",
        name = "Boris Capitanu",
        email = "capitanu@illinois.edu",
        url = url("https://github.com/borice")
      )
    )
  )


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "org.hathitrust.htrc.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "org.hathitrust.htrc.binders._"
