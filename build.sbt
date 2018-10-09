import jp.pigumer.sbt.cloud.aws.cloudformation.{Alias, AwscfSettings, CloudformationStack, Stacks}

lazy val Region: String = "ap-northeast-1"
lazy val BucketName: Option[String] = sys.env.get("BUCKET_NAME")
lazy val KeyName: String = sys.env("KEY_NAME")

lazy val root = (project in file("."))
  .settings(
    organization := "com.pigumer.snp",
    name := "ec2-sample",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.6"
  )
  .enablePlugins(JavaServerAppPackaging, SystemVPlugin, RpmPlugin)
  .settings(
    rpmVendor := "pigumergroup",
    rpmLicense := Some("MIT")
  )
  .enablePlugins(CloudformationPlugin)
  .settings(
    awscfSettings := AwscfSettings(
      region = Region,
      bucketName = BucketName,
      templates = Some(file("cloudformation"))
    )
  )
  .settings(
    awscfStacks := Stacks(
      Alias("ec2") → CloudformationStack(
        stackName = "snp-ec2-stack",
        template = "ec2.yaml",
        parameters = Map(
          "ImageId" → "ami-06cd52961ce9f0d85",
          "KeyName" → KeyName
        ),
        capabilities = Seq("CAPABILITY_NAMED_IAM")
      )
    )
  )