# DevFactory DevSpaces Development Environment

**DevFactory DevSpaces Development Environment** (DE) allows developers to run the Radon Java Obfuscator Application.

## Local Setup

To make use of the DE, a preliminary Local Setup is required. Please proceed with the following instructions:

[dfds-ui]: https://www.devspaces.io
[dfds-faq]: https://devspaces.io/devspaces/help
[dfds-support]: https://devspaces.kayako.com
[app-repo]: https://github.com/trilogy-group/Radon
[sync-ui]: http://localhost:49152/

1. Login in [DevSpaces UI][dfds-ui] with your GitHub Account. The first time you will have to authorize some permissions to DevFactory in order to work properly.

2. Once logged in, install the DevSpaces CLI tools by clicking the link inside the warning message:

    >*The DevSpaces Client is not running. Please...*

3. Clone the Repository [Radon App][app-repo], now the `APP_REPO`.

## Create the Docker Container Configuration

With Local Setup in place, now it's possible to create the Docker Container Configuration in your DevSpaces. Please proceed with the following commands:

    cd APP_REPO/devspaces/docker
    devspaces create

As a output you will see the following:

    / Starting DevSpace creation ...

    - DevSpace trilogy-radon was created. The build/validation process is now in progress

After that, a popup window `'trilogy-radon Build Status'` gets up and displays logs for the building process.

You must wait for the build/validation process to finish before continuing on to the next Section and it may take some time. At the end of the building process, a success message will appear like:

>*`Phase complete: POST_BUILD Success: true`*

This popup window could stop listen for build events and seems stuck. In that case and at any time you can:

1. Resume the listening by clicking the Link `'Resume Tailing Log'` located in the lower left corner.
2. Close and reopen it with the command:

        devspaces info trilogy-radon --buildstatus

On the other hand, the validation process also happens asynchronously, so you should wait for a successful validation message in your email inbox or in the Taskbar Notification Area.

### What Does The 'create' Command Do?

1. Create a new Docker Container Configuration called `trilogy-radon` in your DevSpaces with information coming from the file `devspaces.yml` and the `Dockerfile` itself.

2. Trigger the aforementioned build/validation process.

At the end of this process you can see the Configuration in the [DevSpaces User Interface][dfds-ui] with status `Stopped`, but in the middle with status `Building`, `Creating` and `Validating`.

## Start the Docker Container

With the Docker Container Configuration in place and validated, now it's possible to start the Docker Container with the Development Servers running on it. Please proceed with the following command:

    devspaces start trilogy-radon

As a output you will see something like:

    - DevSpace trilogy-radon instance is starting and will synchronized with your local folder: C:\Users\Jaime Bravo\AppData\Roaming\DevSpaces\devfactory\DevSpaces\trilogy-radon after this DevSpace is ready

### What Does The 'start' Command Do?

1. Create and Start a new Docker Container.
2. Bind a Local Tempory Folder (created on the fly) to the Remote Folder `/data`. We change this in the next Section.

At the end of its execution you can see the `trilogy-radon` Configuration in the [DevSpaces User Interface][dfds-ui] with status `Running`.

## Bind to the Docker Container

With the Docker Container running, now it's possible to bind the Local Repository `APP_REPO`. Please proceed with the following commands:

    cd APP_REPO
    devspaces bind trilogy-radon

As a output you will see something like:

    / Syncing DevSpace trilogy-radon...

    - The directory 'APP_REPO' will now be synchronized with DevSpace trilogy-radon

### What Does The 'bind' Command Do?

Bind (rsync) your Local Repository Folder to the Container's Folder `/data`, the Remote Repository Path.

NOTES:

1. You can see the status and progress of synchronization through this [Local Web UI][sync-ui].
2. Sometimes this command could take time because the rsync (which can be optimized).

## Connect to the Docker Container

Now with the Container running and the Local Repository binded, it's possible to get inside of it, where more commands needs to be execute in order to get all the thing up and running properly. For that, just execute the following command:

    devspaces exec trilogy-radon

### What Does The 'exec' Command Do?

1. Open a popup window `'DevSpaces exec -- Bash ******...'`.
2. Start a new shell as `root` user inside the Docker Container.

When you finish working with the Docker Container, type `exit`.

## Inside the Container

Now inside the Container, it's possible to build and run the App.

    mvn clean install
    cd radon-program/target
    java -jar radon-program-1.0.5.jar --help

## Query Docker Container Info

To see the Docker Container Information, just:

    devspaces info trilogy-radon

An output example of this after starting the Docker Container is as follows:

        DevSpace     : trilogy-radon
        Folder       : F:\trabajo\git\trilogy\fork\Radon
        Status       : Active
        Created Date : 2019-02-20
        Start Date   : 2019-02-20
        Uptime       : 00:01 Hours
        Docker Image : Docker image built for this DevSpace 6 minutes ago by jaime-bravo

        Ports
        You don't have any ports added yet

        Environment Variables
        You don't have any environment variables added yet

## Stop Docker Container

To stop the Docker Container, just:

    devspaces stop trilogy-radon

As a output you will see something like:

    / Stopping DevSpace trilogy-radon...

    - DevSpace trilogy-radon is stopping

After a while you can see the `trilogy-radon` Configuration in the [DevSpaces User Interface][dfds-ui] with status `Stopped` again.

## Rebinding Folders

If you suspect about synchronization stop working fine, you can rebind by just following the instruccions from `'Bind to the Docker Container'` Section again.

## Optimizing File Synchronization

When you bind your Local Repository Folder `APP_REPO` to the Container's Folder `/data`, all the files will be synchronized by default. If you think there is no point in synchronizing some specific folders or files, such as those temporarily generated by an IDE or some Build Tool Executions, then here you have a room for optimization.

So to exclude such files, just include paths or glob patterns inside the `sync-ignore` directive from `APP_REPO/devspaces/docker/devspaces.yml` file.

## About the DevSpaces User Interface

The [DevSpaces User Interface][dfds-ui] allows you to see the Docker Container Configurations authored by you and the status of the Docker Containers launched from those configurations inside your DevSpaces. In the case of Radon Java Obfuscator Application, you can observe the following when you are logged in:

The Main Section `My DevSpaces` showing your Docker Container Configuration for the Application called `trilogy-radon`. Its row contains a button where you can manage the Docker Container, see and modify the Configuration details.

## Running Application via Docker-Compose File

### Requirements

* Docker.
* Docker Compose Tool.
* Bash Shell.

### Docker Container Management

The following commands handle the life cycle of a Docker Container Service called `trilogy-radon` and are quite similar to those already explained:

|Command|Action|
|-------|------|
|`./docker-cli.sh deploy`|Deploy & Start a New Docker Container Service|
|`./docker-cli.sh exec`|Connect to the Docker Container|
|`./docker-cli.sh stop`|Stop the Docker Container Service|
|`./docker-cli.sh start`|Start the Docker Container Service (should be used only after `stop` command)|
|`./docker-cli.sh undeploy`|Stop & Undeploy the Docker Container Service|

## Troubleshooting

### DevSpaces Creation ends with a Build Failed Status

An unexpected status of `Build Failed` could be observed even though the Build Logs shows a Successful Build. In such case it's necessary retry the whole process, doing the following:

    cd APP_REPO/devspaces/docker
    devspaces update

### Unauthorized on DevSpaces Exec Command

The following unexpected result could be output by execution of the command `devspaces exec trilogy-radon`:

    error: You must be logged in to the server (Unauthorized)

    x Something is wrong with your DevSpace, try to execute the command again, or try to restart the DevSpace!

To overcome this problem, follow the steps below:

1. Exit from the DevSpaces CLI Tool from the Taskbar.
2. Log out from the [DevSpaces User Interface][dfds-ui] and close the browser.
3. Log in again with your Github Account.
4. Try twice the same command `devspaces exec trilogy-radon`.
    * The first time, it launches the CLI Tool but doesn't execute the command itself.
    * The second time is actual command execution.

### Other Issues

In case of any other problem, doubt or just by curiosity:

* Visit the [DevSpaces Documentation][dfds-faq].
* Submit a request for help on [DevSpaces Support Web Site][dfds-support].