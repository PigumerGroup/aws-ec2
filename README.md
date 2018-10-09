sbt-native-packager ec2-sample
==============================

# Build

```
$ docker run -it --rm -v `pwd`:/src -v "$HOME/.ivy2":/root/.ivy2 pigumergroup/docker-sbt:debian-plus-n /bin/sh
# apt update; apt install alien -y
# cd src
# sbt ";clean ;rpm:packageBin"
# exit
$ KEY_NAME=<YOUR KEY PAIR NAME> BUCKET_NAME=<YOUR BUCKET NAME> sbt
sbt> ;uploadRpm ;awscfUploadTemplates ; awscfCreateStack ec2
sbt> exit
```

# Deploy and Run

```
$ ssh -i <YOUR KEY PAIR PATH>.pem \
    ec2-user@<YOUR EC2 Instance DNS>
[ec2-user]$ cat /var/log/ec2-sample/ec2-sample.log
Hello World!!
[ec2-user]$ exit
```

# Delete Stacks

```
$ KEY_NAME=<YOUR KEY NAME> BUCKET_NAME=<YOUR BUCKET NAME> sbt
sbt> awscfDeleteStack ec2
sbt> exit
```
