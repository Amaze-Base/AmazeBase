package de.ironicdev.amazebase;

import de.ironicdev.amazebase.models.Client;
import de.ironicdev.amazebase.models.Project;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Project> initMockData() {
        List<Project> projects = new ArrayList<>();
        Project project = new Project();
        project.setApiKey("0b49a6abaf74c312a622d851d3b45f5028fe85c88813dd849ed7721675a2d916"); // sampleDb
        project.getClientList().add(new Client("114792137e21c52d46c2763184efdb92ca3a122058eddba81d15b3b21a80c288")); // javaTestClient
        project.setProjectName("sampleDb");
        projects.add(project);

        Project project2 = new Project();
        project2.setApiKey("e3f56bae4b7e1cf8db3fd4ad65f10d19ce4763e3e4774caa3842cdd1a75d8789"); // jasna
        project2.getClientList().add(new Client("888e6b533d0808ad3b9688f206eb1abc2d147b5f8c3d12acb942d46f6dafd372")); // jasna-java-client
        project2.setProjectName("jasna");
        projects.add(project2);

        return projects;
    }

}
