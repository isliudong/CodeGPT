package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.actions.CreateNewConversationAction;
import ee.carlrobert.codegpt.toolwindow.chat.actions.OpenInEditorAction;
import ee.carlrobert.codegpt.toolwindow.chat.actions.UsageToolbarLabelAction;
import org.jetbrains.annotations.NotNull;

public class ChatToolWindowPanel extends SimpleToolWindowPanel {

  public ChatToolWindowPanel(@NotNull Project project) {
    super(true);
    initialize(project);
  }

  private void initialize(Project project) {
    var tabPanel = new ChatToolWindowTabPanel(project);
    var conversation = ConversationsState.getCurrentConversation();
    if (conversation == null) {
      tabPanel.displayLandingView();
    } else {
      tabPanel.displayConversation(conversation);
    }

    var tabbedPane = createTabbedPane(tabPanel);
    setToolbar(createActionToolbar(project, tabbedPane).getComponent());
    setContent(tabbedPane);
  }

  private ActionToolbar createActionToolbar(Project project, ChatTabbedPane tabbedPane) {
    var actionGroup = new DefaultActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new CreateNewConversationAction(() -> {
      var panel = new ChatToolWindowTabPanel(project);
      panel.setConversation(ConversationsState.getInstance().startConversation());
      panel.displayLandingView();
      tabbedPane.addNewTab(panel);
      repaint();
      revalidate();
    }));
    actionGroup.add(new OpenInEditorAction());
    actionGroup.addSeparator();
    actionGroup.add(new UsageToolbarLabelAction());

    // TODO: Data usage not enabled in stream mode https://community.openai.com/t/usage-info-in-api-responses/18862/11
    // actionGroup.add(new TokenToolbarLabelAction());

    return ActionManager.getInstance().createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, false);
  }

  private ChatTabbedPane createTabbedPane(ChatToolWindowTabPanel tabPanel) {
    var tabbedPane = new ChatTabbedPane();
    tabbedPane.addNewTab(tabPanel);
    return tabbedPane;
  }
}
