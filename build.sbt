import jp.pigumer.sbt.cloud.aws.cloudformation.{Alias, AwscfSettings, CloudformationStack, Stacks}

lazy val Region: String = "ap-northeast-1"
lazy val BucketName: Option[String] = sys.env.get("BUCKET_NAME")
lazy val KeyName: String = sys.env("KEY_NAME")

val uploadRpm = taskKey[Unit]("Upload rpm")

lazy val root = (project in file("."))
  .settings(
    organization := "com.pigumer.snp",
    name := "ec2-sample",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.6"
  )
  .enablePlugins(JavaServerAppPackaging, UpstartPlugin, RpmPlugin)
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
    awscfStacks := {
      val pkg = (packageName in Rpm).value
      val ver = (version in Rpm).value
      val arch = (packageArchitecture in Rpm).value
      val name = s"$pkg-$ver.$arch.rpm"
      val key = s"target/$name"
      Stacks(
        Alias("ec2") → CloudformationStack(
          stackName = "snp-ec2-stack",
          template = "ec2.yaml",
          parameters = Map(
            "ImageId" → "ami-06cd52961ce9f0d85",
            "KeyName" → KeyName,
            "RpmBucket" → BucketName.get,
            "RpmKey" → key,
            "RpmName" → name
          ),
          capabilities = Seq("CAPABILITY_NAMED_IAM")
        )
      )
    }
  )
  .settings(
    uploadRpm := {
      val log = streams.value.log
      val pkg = (packageName in Rpm).value
      val ver = (version in Rpm).value
      val arch = (packageArchitecture in Rpm).value
      val fileName = s"$pkg-$ver.$arch.rpm"
      val s3 = awss3.value
      s3.putObject(BucketName.get, s"target/$fileName", file(s"target/rpm/RPMS/$arch/$fileName"))
    }
  )