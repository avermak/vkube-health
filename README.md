# vkube-health
A JSON/REST style endpoint to be deployed in a Kubernetes cluster.
Provides pertinent health information about the node.

Some of the key pieces of information returned include:
* Pod name - Host name of the pod as assigned by Kubernetes.
* Node name - Host name of the node on which that given pod is running.
* Node ip - IP address of the node on which that given pod is running.
* Pod time - Epoch time of the pod.
* CPU temperatures - Array of temperatures of the CPU cores as made available by the underlying OS running in the pod.
* Total memory - Total memory available to the pod.
* Free memory - Memory currently free on the pod.
* CPU usage - User and System % CPU usage.
* CPU cores - number of CPU cores visible to the pod.

