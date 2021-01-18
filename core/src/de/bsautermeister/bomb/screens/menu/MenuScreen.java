package de.bsautermeister.bomb.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.BombGame;
import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.service.ServiceKeys;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.audio.MusicPlayer;
import de.bsautermeister.bomb.core.GameApp;
import de.bsautermeister.bomb.core.ScreenBase;
import de.bsautermeister.bomb.screens.game.GameScreen;
import de.bsautermeister.bomb.screens.menu.content.AboutContent;
import de.bsautermeister.bomb.screens.menu.content.MenuContent;
import de.bsautermeister.bomb.screens.transition.ScreenTransitions;
import de.bsautermeister.bomb.utils.GdxUtils;
import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.leaderboard.IFetchLeaderBoardEntriesResponseListener;
import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;

public class MenuScreen extends ScreenBase {
    private final static Logger LOG = new Logger(MenuScreen.class.getSimpleName(), Cfg.LOG_LEVEL);

    private final Viewport uiViewport;
    private Stage stage;

    private Table content;

    private final String initialContentType;

    public MenuScreen(GameApp game) {
        this(game, MenuContent.TYPE);
    }

    public MenuScreen(GameApp game, String contentType) {
        super(game);
        this.uiViewport = new StretchViewport(Cfg.Ui.WIDTH, Cfg.Ui.HEIGHT);
        this.initialContentType = contentType;
    }

    @Override
    public void show() {
        stage = new Stage(uiViewport, getGame().getBatch());
        stage.setDebugAll(Cfg.DEBUG_MODE);

        setContent(createContent(initialContentType));

        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        BombGame game = (BombGame) getGame();
        if (!game.getMusicPlayer().isSelected(Assets.Music.MENU_SONG)) {
            game.getMusicPlayer().selectSmoothLoopedMusic(Assets.Music.MENU_SONG, 46.5f);
            game.getMusicPlayer().setVolume(MusicPlayer.MAX_VOLUME, true);
            game.getMusicPlayer().playFromBeginning();
        }

        fetchData();
    }

    private void fetchData() {
        final BombGame bombGame = (BombGame) getGame();

        bombGame.getGameServiceClient().fetchLeaderboardEntries(
                ServiceKeys.Scores.MAX_DEPTH, 1, true,
                new IFetchLeaderBoardEntriesResponseListener() {
                    @Override
                    public void onLeaderBoardResponse(Array<ILeaderBoardEntry> leaderBoard) {
                        if (!leaderBoard.isEmpty()) {
                            ILeaderBoardEntry entry = leaderBoard.get(0);
                            int scoreValue = (int) entry.getSortValue();
                            bombGame.getGameScores().updatePersonalBest(scoreValue);
                        }
                    }
                });

        bombGame.getGameServiceClient().fetchLeaderboardEntries(
                ServiceKeys.Scores.MAX_DEPTH, 10, false,
                new IFetchLeaderBoardEntriesResponseListener() {
                    @Override
                    public void onLeaderBoardResponse(Array<ILeaderBoardEntry> leaderBoard) {
                        Array<Integer> scores = new Array<>(10);
                        for (ILeaderBoardEntry entry : leaderBoard) {
                            if (!entry.isCurrentPlayer()) {
                                int scoreValue = (int) entry.getSortValue();
                                scores.add(scoreValue);
                            }
                        }
                        bombGame.getGameScores().updateTopList(scores);
                    }
                });
    }

    private void setContent(Table newContent) {
        if (content != null) {
            content.addAction(Actions.sequence(
                    Actions.removeActor()
            ));
        }
        content = newContent;
        stage.addActor(newContent);
    }

    private Table createContent(String contentType) {
        if (MenuContent.TYPE.equals(contentType)) {
            return createMainContent();
        } else if (AboutContent.TYPE.equals(contentType)) {
            return createAboutContent();
        }

        throw new IllegalArgumentException("Unknown content type");
    }

    private Table createMainContent() {
        return new MenuContent(
                getAssetManager(),
                ((BombGame) getGame()).getGameSettings(),
                ((BombGame) getGame()).getGameScores(),
                new MenuContent.Callbacks() {
                    @Override
                    public void playClicked() {
                        setScreen(new GameScreen(getGame(), false), ScreenTransitions.SLIDE_DOWN);
                    }

                    @Override
                    public void continueClicked() {
                        setScreen(new GameScreen(getGame(), true), ScreenTransitions.SLIDE_DOWN);
                    }

                    @Override
                    public void leaderboardsClicked() {
                        try {
                            getGame().getGameServiceClient().showLeaderboards(
                                    ServiceKeys.Scores.MAX_DEPTH);
                        } catch (GameServiceException e) {
                            LOG.error("Showing leaderboards failed", e);
                        }
                    }

                    @Override
                    public void achievementsClicked() {
                        try {
                            getGame().getGameServiceClient().showAchievements();
                        } catch (GameServiceException e) {
                            LOG.error("Showing achievements failed", e);
                        }
                    }

                    @Override
                    public void aboutClicked() {
                        setContent(createAboutContent());
                    }

                    @Override
                    public void rateClicked() {

                    }
                },
                ((BombGame) getGame()).getGameFile().exists(),
                getGame().getGameServiceClient().isFeatureSupported(
                        IGameServiceClient.GameServiceFeature.ShowLeaderboardUI),
                getGame().getGameServiceClient().isFeatureSupported(
                        IGameServiceClient.GameServiceFeature.ShowAchievementsUI));
    }

    private Table createAboutContent() {
        return new AboutContent(getAssetManager());
    }

    @Override
    public void render(float delta) {
        GdxUtils.clearScreen(Color.BLACK);

        // ensure background tint color is not affected by actor actions
        getBatch().setColor(Color.WHITE);

        stage.act();
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
                || (content instanceof AboutContent && Gdx.input.justTouched())) {
            if (content instanceof MenuContent) {
                Gdx.app.exit();
            } else {
                setContent(createMainContent());
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
