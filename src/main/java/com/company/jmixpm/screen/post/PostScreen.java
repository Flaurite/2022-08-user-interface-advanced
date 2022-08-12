package com.company.jmixpm.screen.post;

import com.company.jmixpm.entity.Post;
import com.company.jmixpm.entity.PostService;
import com.company.jmixpm.screen.userinfo.UserInfoScreen;
import io.jmix.core.LoadContext;
import io.jmix.ui.ScreenBuilders;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.Table;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Route("posts")
@UiController("PostScreen")
@UiDescriptor("post-screen.xml")
public class PostScreen extends Screen {

    @Autowired
    private PostService postService;

    @Autowired
    private Table<Post> postsTable;

    @Install(to = "pagination", subject = "totalCountDelegate")
    private Integer paginationTotalCountDelegate() {
        return postService.postsTotalCount();
    }

    @Autowired
    private ScreenBuilders screenBuilders;

    @Install(to = "userInfoScreen", subject = "screenConfigurer")
    private void userInfoScreenScreenConfigurer(UserInfoScreen userInfoScreen) {
        Post post = postsTable.getSingleSelected();
        if (post == null || post.getUserId() == null) {
            return;
        }

        userInfoScreen.withUserId(post.getUserId());
    }

    @Install(to = "postsDl", target = Target.DATA_LOADER)
    private List<Post> postsDlLoadDelegate(LoadContext<Post> loadContext) {
        Post[] posts =
                postService.fetchPosts(
                        loadContext.getQuery().getFirstResult(),
                        loadContext.getQuery().getMaxResults());
        return Arrays.asList(posts);
    }

  /*  @Subscribe("postsTable.viewUserInfo")
    public void onPostsTableViewUserInfo(Action.ActionPerformedEvent event) {
        Post post = postsTable.getSingleSelected();
        if (post == null || post.getUserId() == null) {
            return;
        }

        screenBuilders.screen(this)
                .withScreenClass(UserInfoScreen.class)
                .build()
                .withUserId(post.getUserId())
                .show();
    }*/
}