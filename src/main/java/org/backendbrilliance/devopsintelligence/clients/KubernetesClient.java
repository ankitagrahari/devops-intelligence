package org.backendbrilliance.devopsintelligence.clients;

import io.kubernetes.client.Metrics;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.custom.ContainerMetrics;
import io.kubernetes.client.custom.NodeMetrics;
import io.kubernetes.client.custom.NodeMetricsList;
import io.kubernetes.client.custom.PodMetrics;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.KubectlPortForward;
import io.kubernetes.client.extended.kubectl.KubectlTop;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 *
 * Ref Source: https://github.com/kubernetes-client/java/blob/master/examples/
 */
public interface KubernetesClient {

    static Class<? extends KubernetesObject> getClassForKind(String kind) {
        return switch (kind) {
            case "pod", "pods" -> V1Pod.class;
            case "deployment", "deployments" -> V1Deployment.class;
            case "service", "services" -> V1Service.class;
            case "node", "nodes" -> V1Node.class;
            case "replicationcontroller", "replicationcontrollers" -> V1ReplicationController.class;
            default -> null;
        };
    }

    String PADDING = "                              ";

    private static String pad(String value) {
        while (value.length() < PADDING.length()) {
            value += " ";
        }
        return value;
    }

    default KubernetesObject delete(String kind, String ns, String name) throws KubectlException {
        return Kubectl.delete(getClassForKind(kind))
                .namespace(ns)
                .name(name)
                .execute();
    }

    default KubernetesObject drain(ApiClient apiClient, String name) throws KubectlException, IOException {
        return Kubectl.drain()
                .apiClient(apiClient)
                .name(name)
                .execute();
    }

    default void topNodes(ApiClient apiClient) throws KubectlException {
        List<Pair<V1Node, NodeMetrics>> nodes = Kubectl
                .top(V1Node.class, NodeMetrics.class)
                .apiClient(apiClient)
                .metric("cpu")
                .execute();
        System.out.println(pad("Node") + "\tCPU\t\tMemory");

        for (Pair<V1Node, NodeMetrics> node : nodes) {
            System.out.println(
                    pad(Objects.requireNonNull(
                            Objects.requireNonNull(node.getLeft().getMetadata())
                                    .getName()))
                            + "\t"
                            + node.getRight().getUsage().get("cpu").getNumber()
                            + "\t"
                            + node.getRight().getUsage().get("memory").getNumber());
        }
    }

    default void topPods(ApiClient apiClient, String ns) throws KubectlException {
        List<Pair<V1Pod, PodMetrics>> pods = Kubectl
                .top(V1Pod.class, PodMetrics.class)
                        .apiClient(apiClient)
                        .namespace(ns)
                        .metric("cpu")
                        .execute();
        System.out.println(pad("Pod") + "\tCPU\t\tMemory");
        for (Pair<V1Pod, PodMetrics> pod : pods) {
            System.out.println(
                    pad(Objects.requireNonNull(
                            Objects.requireNonNull(pod.getLeft().getMetadata())
                                    .getName()))
                            + "\t"
                            + KubectlTop.podMetricSum(pod.getRight(), "cpu")
                            + "\t"
                            + KubectlTop.podMetricSum(pod.getRight(), "memory"));
        }
    }

    default void portForwarding(ApiClient apiClient, String ns, String name, String port) throws KubectlException {
        //port: 8080:8080
        KubectlPortForward forward = Kubectl.portforward()
                .apiClient(apiClient)
                .name(name)
                .namespace(ns);

        String[] ports = port.split(":");
        System.out.println("Forwarding " + ns + "/" + name + " " + ports[0] + "->" + ports[1]);
        forward.ports(Integer.parseInt(ports[0]), Integer.parseInt(ports[1]));
        forward.execute();
    }

    default void scale(ApiClient apiClient, String kind, String ns, String name, Integer replicas) throws KubectlException {
        Kubectl.scale(getClassForKind(kind))
                .apiClient(apiClient)
                .namespace(ns)
                .name(name)
                .replicas(replicas)
                .execute();
    }

    default String getVersion(ApiClient apiClient) throws KubectlException {
        return Kubectl.version()
                .apiClient(apiClient)
                .execute()
                .toString();
    }

    default Integer execute(ApiClient apiClient, String ns, String name, String container, String[] command) throws KubectlException {
        return Kubectl.exec()
                .apiClient(apiClient)
                .namespace(ns)
                .name(name)
                .command(command)
                .container(container)
                .execute();
    }

    default void apiResources(ApiClient apiClient) throws KubectlException {
        Kubectl.apiResources()
                .apiClient(apiClient)
                .execute()
                .forEach(r ->
                    System.out.printf(
                            "%s\t\t%s\t\t%s\t\t%s\n",
                            r.getResourcePlural(), r.getGroup(), r.getKind(), r.getNamespaced()));
    }

    default KubernetesObject addLabels(ApiClient apiClient, String kind, String ns, String name, String labelKey, String labelValue) throws KubectlException {
        Class<? extends KubernetesObject> clazz = getClassForKind(kind);
        if (clazz == null) {
            System.err.println("Unknown kind: " + kind);
            System.exit(-2);
        }
        return Kubectl.label(clazz)
                .apiClient(apiClient)
                .namespace(ns)
                .name(name)
                .addLabel(labelKey, labelValue)
                .execute();
    }

    default V1Node cordon(ApiClient apiClient, String name) throws KubectlException {
        return Kubectl.cordon()
                .apiClient(apiClient)
                .name(name)
                .execute();
    }

    default V1Node uncordon(ApiClient apiClient, String name) throws KubectlException {
        return Kubectl.uncordon()
                .apiClient(apiClient)
                .name(name)
                .execute();
    }

    default Boolean copy(ApiClient apiClient, String ns, String name, String container, String from, String to) throws KubectlException {
        return Kubectl.copy()
                .apiClient(apiClient)
                .namespace(ns)
                .name(name)
                .container(container)
                .fromPod(from)
                .to(to)
                .execute();
    }

    default void getNodeMetrics(Metrics metrics) throws ApiException {
        NodeMetricsList list = metrics.getNodeMetrics();
        for (NodeMetrics item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
            System.out.println("------------------------------");
            for (String key : item.getUsage().keySet()) {
                System.out.println("\t" + key);
                System.out.println("\t" + item.getUsage().get(key));
            }
            System.out.println();
        }
    }

    default void getPodMetrics(Metrics metrics) throws ApiException {
        for (PodMetrics item : metrics.getPodMetrics("default").getItems()) {
            System.out.println(item.getMetadata().getName());
            System.out.println("------------------------------");
            if (item.getContainers() == null) {
                continue;
            }
            for (ContainerMetrics container : item.getContainers()) {
                System.out.println(container.getName());
                System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                for (String key : container.getUsage().keySet()) {
                    System.out.println("\t" + key);
                    System.out.println("\t" + container.getUsage().get(key));
                }
                System.out.println();
            }
        }
    }

    default void yamlLoad(ApiClient apiClient, String ns, String name) throws IOException, ApiException {
        // Example yaml file can be found in $REPO_DIR/test-svc.yaml
        File file = new File("src/main/resources/test-svc.yaml");
        V1Service yamlSvc = (V1Service) Yaml.load(file);

        // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
        CoreV1Api api = new CoreV1Api();
        V1Service createResult = api
                .createNamespacedService(ns, yamlSvc)
                .execute();
        System.out.println(createResult);

        assert Objects.requireNonNull(yamlSvc.getMetadata()).getName() != null;

        V1Service deleteResult = api
                .deleteNamespacedService(yamlSvc.getMetadata().getName(), ns)
                .execute();
        System.out.println(deleteResult);
    }
}
