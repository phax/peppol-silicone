@echo off
call mvn deploy -DaltDeploymentRepository=repo.phloc.public::default::sftp://www.phloc.com/var/www/html-repo/maven2
