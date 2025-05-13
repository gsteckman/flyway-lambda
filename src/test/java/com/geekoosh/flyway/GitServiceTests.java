package com.geekoosh.flyway;

import com.geekoosh.flyway.request.GitRequest;
import com.geekoosh.lambda.git.GitService;
import org.eclipse.jgit.junit.http.AppServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Objects;

@RunWith(MockitoJUnitRunner.class)
public class GitServiceTests extends GitSSLTestCase {
    @Test
    public void reuseGitRepo() throws Exception {
        GitRequest gitRequest = new GitRequest()
                .setReuseRepo(true)
                .setGitBranch("master")
                .setGitRepository(getRepoUrl())
                .setUsername(AppServer.username)
                .setPassword(AppServer.password);
        GitService gitService = new GitService(gitRequest);
        try {
            pushFilesToMaster(
                    List.of(
                            new GitFile(
                                    Objects.requireNonNull(getClass().getClassLoader().getResource("migrations/mysql/V1__init.sql")),
                                    "V1__init.sql"
                            )
                    )
            );
            gitService.prepare();
            Assert.assertTrue(gitService.hasFile("V1__init.sql"));
            Assert.assertFalse(gitService.hasFile("V2__update.sql"));
            gitService.clean();
            Assert.assertTrue(gitService.hasFile("V1__init.sql"));
            pushFilesToMaster(
                    List.of(
                            new GitFile(
                                    Objects.requireNonNull(getClass().getClassLoader().getResource("migrations/mysql/V2__update.sql")),
                                    "V2__update.sql"
                            )
                    )
            );
            gitService.prepare();
            Assert.assertTrue(gitService.hasFile("V1__init.sql"));
            Assert.assertTrue(gitService.hasFile("V2__update.sql"));
        } finally {
            gitService.forceClean();
        }
    }
}
