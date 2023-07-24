export JAVA_HOME=/Users/salihburakdemirci/Library/Java/JavaVirtualMachines/openjdk-19.0.1/Contents/Home
mvn clean
mvn package -DskipTests
docker build --tag gcr.io/sbd-crypto/backend .
docker push gcr.io/sbd-crypto/backend
gcloud run deploy backend --region asia-northeast1 --image gcr.io/sbd-crypto/backend:latest
