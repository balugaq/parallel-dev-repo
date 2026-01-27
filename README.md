# Parallel Dev Repo

This project allows Rebar and Rebar to be developed in parallel. To set up the subprojects, simply run `./gradlew`.
Rebar and Pylon are each their own Git repository. Therefore, any changes made to them must be committed and pushed separately.

## Tasks

'snapshot' = your local version
'stable' = latest release

- `./gradlew runServer` starts a server with snapshot versions of Rebar and Pylon.
- `./gradlew runStableServer` starts a server with snapshot Rebar and stable Pylon.
- `./gradlew runLiveTests` runs the live tests against a server with snapshot Pylon.
