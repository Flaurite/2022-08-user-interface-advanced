package com.company.jmixpm.screen.user;

import com.company.jmixpm.entity.User;
import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.app.inputdialog.DialogActions;
import io.jmix.ui.app.inputdialog.InputDialog;
import io.jmix.ui.app.inputdialog.InputParameter;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.NotificationFacet;
import io.jmix.ui.component.TextArea;
import io.jmix.ui.executor.BackgroundTask;
import io.jmix.ui.executor.BackgroundTaskHandler;
import io.jmix.ui.executor.BackgroundWorker;
import io.jmix.ui.executor.TaskLifeCycle;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import io.jmix.ui.settings.ScreenSettings;
import io.jmix.ui.settings.facet.ScreenSettingsFacet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@UiController("User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {
    private static final Logger log = LoggerFactory.getLogger(UserBrowse.class);

    @Autowired
    private Dialogs dialogs;

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private GroupTable<User> usersTable;

    @Autowired
    private CollectionContainer<User> usersDc;

    @Autowired
    private Notifications notifications;

    @Autowired
    private BackgroundWorker backgroundWorker;

    @Autowired
    private NotificationFacet taskDoneNotification;

    @Subscribe("emailInputDialog")
    public void onEmailInputDialogInputDialogClose(InputDialog.InputDialogCloseEvent event) {
        String title = event.getValue("title");
        String body = event.getValue("body");
        Set<User> selected = usersTable.getSelected();
        Collection<User> users = selected.isEmpty()
                ? usersDc.getItems()
                : selected;
        doSendEmail(title, body, users);
    }

    @Subscribe("usersTable.sendEmail")
    public void onUsersTableSendEmail(Action.ActionPerformedEvent event) {
        /*dialogs.createInputDialog(this)
                .withCaption("Send email")
                .withParameters(
                        InputParameter.stringParameter("title")
                                .withCaption("Title")
                                .withRequired(true),
                        InputParameter.stringParameter("body")
                                .withField(() -> {
                                    TextArea<String> textArea = uiComponents.create(TextArea.NAME);
                                    textArea.setCaption("Body");
                                    textArea.setRequired(true);
                                    textArea.setWidthFull();
                                    return textArea;
                                }))
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(eventClose -> {
                    String title = eventClose.getValue("title");
                    String body = eventClose.getValue("body");

                    Set<User> selected = usersTable.getSelected();
                    Collection<User> users = selected.isEmpty()
                            ? usersDc.getItems()
                            : selected;

                    doSendEmail(title, body, users);
                })
                .show();*/
    }

    private void doSendEmail(String title, String body, Collection<User> users) {
        BackgroundTask<Integer, Void> task = new EmailTask(title, body, users);

//        BackgroundTaskHandler<Void> taskHandler = backgroundWorker.handle(task);
//        taskHandler.execute();
//        taskHandler.getResult();
//        log.info("Got result from background task");

        dialogs.createBackgroundWorkDialog(this, task)
                .withCaption("Sending reminder emails")
                .withMessage("Please wait while emails are being sent")
                .withTotal(users.size())
                .withShowProgressInPercentage(true)
                .withCancelAllowed(true)
                .show();
    }

    private class EmailTask extends BackgroundTask<Integer, Void> {

        private String title;
        private String body;
        private Collection<User> users;

        public EmailTask(String title, String body, Collection<User> users) {
            super(10, TimeUnit.MINUTES, UserBrowse.this);

            this.title = title;
            this.body = body;
            this.users = users;
        }

        @Override
        public Void run(TaskLifeCycle<Integer> taskLifeCycle) throws Exception {
            int i = 0;
            for (User user : users) {
                if (taskLifeCycle.isInterrupted()
                        || taskLifeCycle.isCancelled()) {
                    return null;
                }

                TimeUnit.SECONDS.sleep(2);

                i++;
                taskLifeCycle.publish(i);
            }
            return null;
        }

        @Override
        public void done(Void result) {
            taskDoneNotification.show();
        }
    }
}