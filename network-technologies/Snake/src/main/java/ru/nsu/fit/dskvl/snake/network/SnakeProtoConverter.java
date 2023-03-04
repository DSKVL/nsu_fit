package ru.nsu.fit.dskvl.snake.network;

import com.google.protobuf.InvalidProtocolBufferException;
import me.ippolitov.fit.snakes.SnakesProto.*;
import ru.nsu.fit.dskvl.snake.SnakeEntity;
import ru.nsu.fit.dskvl.snake.data.*;

import java.util.*;

public class SnakeProtoConverter {

    public enum MessageType {
        NONE,
        ANNOUNCEMENT,
        DISCOVER,
        JOIN,
        STEER,
        STATE
    }

    public static MessageType readGameMessageType(byte[] data) throws InvalidProtocolBufferException {
        GameMessage gameMessage = GameMessage.parseFrom(data);

        if (gameMessage.hasAnnouncement()) {
            return MessageType.ANNOUNCEMENT;
        } else if (gameMessage.hasDiscover()) {
            return MessageType.DISCOVER;
        } else if (gameMessage.hasJoin()) {
            return MessageType.JOIN;
        } else if (gameMessage.hasState()) {
            return MessageType.STATE;
        } else if (gameMessage.hasSteer()) {
            return MessageType.STEER;
        } else {
            return MessageType.NONE;
        }
    }

    public static byte[] writeDiscoverMessage(int msg_seq) {
        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(msg_seq)
                .setDiscover(GameMessage.DiscoverMsg.newBuilder().build())
                .build();

        return gameMessage.toByteArray();
    }

    private static SnakeRole protoToRole(NodeRole role) {
        return switch (role) {
            case MASTER -> SnakeRole.MASTER;
            case VIEWER -> SnakeRole.VIEWER;
            case DEPUTY -> SnakeRole.DEPUTY;
            case NORMAL -> SnakeRole.NORMAL;
        };
    }

    public static SnakeGameAnnouncement readGameAnnouncement(byte[] data) throws InvalidProtocolBufferException {
        GameMessage.AnnouncementMsg protoMessage = GameMessage.parseFrom(data).getAnnouncement();
        int count = protoMessage.getGamesCount();

        SnakeGameAnnouncement snakeAnnouncement = new SnakeGameAnnouncement();
        if (count > 0) {
            GameAnnouncement protoAnnouncement = protoMessage.getGames(0);
            snakeAnnouncement.name = protoAnnouncement.getGameName();
            if (protoAnnouncement.hasCanJoin())
                snakeAnnouncement.canJoin = protoAnnouncement.getCanJoin();
            snakeAnnouncement.players = readPlayers(protoAnnouncement.getPlayers());
            GameConfig gameConfig = protoAnnouncement.getConfig();

            snakeAnnouncement.gameParameters = new SnakeGameParameters(
                        snakeAnnouncement.name,
                        gameConfig.getHeight(),
                        gameConfig.getWidth(),
                        SnakeRole.NORMAL,
                        gameConfig.getStateDelayMs(),
                        gameConfig.getFoodStatic(),
                        1000
                    );
        }
        return snakeAnnouncement;
    }

    private static Map<Integer, SnakePlayer> readPlayers(GamePlayers gamePlayers) {
        Map<Integer, SnakePlayer> players = new HashMap<>();
        for (var protoPlayer: gamePlayers.getPlayersList()) {
            SnakePlayer player = new SnakePlayer(
                    protoPlayer.getId(),
                    protoPlayer.getName(),
                    null,
                    0,
                    protoToRole(protoPlayer.getRole()),
                    1,
                    protoPlayer.getScore()
            );
            players.put(player.id(), player);
        }
        return players;
    }


    private static GamePlayers writeGamePlayers(Map<Integer, SnakePlayer> snakePlayers) {
        GamePlayers gamePlayers = GamePlayers.newBuilder().buildPartial();
        for (var player: snakePlayers.entrySet()) {
            GamePlayer gamePlayer = GamePlayer.newBuilder()
                    .setId(player.getValue().id())
                    .setName(player.getValue().name())
                    .setRole(roleToProto(player.getValue().role()))
                    .setScore(player.getValue().score())
                    .setType(PlayerType.HUMAN)
                    .build();
            gamePlayers.toBuilder().addPlayers(gamePlayer).buildPartial();
        }
        gamePlayers.toBuilder().build();
        return gamePlayers;
    }

    public static byte[] writeAnnouncementMessage(int msg_seq, SnakeGameAnnouncement snakeGameAnnouncement) {
        GamePlayers gamePlayers = writeGamePlayers(snakeGameAnnouncement.players);

        SnakeGameParameters gameParameters = snakeGameAnnouncement.gameParameters;
        GameConfig gameConfig = GameConfig.newBuilder()
                .setWidth(gameParameters.width())
                .setHeight(gameParameters.height())
                .setFoodStatic(gameParameters.foodStatic())
                .setStateDelayMs(gameParameters.delay())
                .build();

        GameAnnouncement gameAnnouncement = GameAnnouncement.newBuilder()
                .setPlayers(gamePlayers)
                .setConfig(gameConfig)
                .setCanJoin(snakeGameAnnouncement.canJoin)
                .setGameName(snakeGameAnnouncement.name)
                .build();

        GameMessage.AnnouncementMsg announcementMsg = GameMessage.AnnouncementMsg.newBuilder()
                .addGames(gameAnnouncement)
                .build();

        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(msg_seq)
                .setAnnouncement(announcementMsg)
                .build();

        return gameMessage.toByteArray();
    }

    private static NodeRole roleToProto(SnakeRole role) {
        return switch (role) {
            case MASTER -> NodeRole.MASTER;
            case VIEWER -> NodeRole.VIEWER;
            case DEPUTY -> NodeRole.DEPUTY;
            case NORMAL -> NodeRole.NORMAL;
            default -> null;
        };
    }

    public static byte[] writeJoinMessage(int msg_seq, String gameName) {
        GameMessage.JoinMsg joinMsg = GameMessage.JoinMsg.newBuilder()
                .setPlayerType(PlayerType.HUMAN)
                .setRequestedRole(NodeRole.NORMAL)
                .setGameName(gameName)
                .setPlayerName("Player " + new Random().nextInt(500))
                .build();

        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(msg_seq)
                .setJoin(joinMsg)
                .build();

        return gameMessage.toByteArray();
    }

    public static SnakeJoinRequest readJoinMsg(byte[] data) throws InvalidProtocolBufferException {
        GameMessage.JoinMsg protoMessage = GameMessage.parseFrom(data).getJoin();
        var role =  protoMessage.hasRequestedRole()
                ? switch (protoMessage.getRequestedRole()) {
                    case NORMAL->SnakeRole.NORMAL;
                    case VIEWER->SnakeRole.VIEWER;
                    default -> SnakeRole.NONE;
                } : SnakeRole.NONE;

        return new SnakeJoinRequest(
                protoMessage.getPlayerName(),
                protoMessage.getGameName(),
                role,
                0,
                null,
                0
        );
    }


    public static SnakeGameState readStateMsg(byte[] data) throws InvalidProtocolBufferException {
        GameState protoState = GameMessage.parseFrom(data).getState().getState();

        int stateNumber = protoState.getStateOrder();

        Map<Integer, SnakeEntity> snakes = new HashMap<>();
        for (var snake: protoState.getSnakesList()) {
            List<Point> body = new ArrayList<>();
            for (var p: snake.getPointsList()) {
                Point point = snakePointFromProto(p);
                body.add(point);
            }

            SnakeEntity.SnakeState state = snakeStateFromProto(snake.getState());
            int id = snake.getPlayerId();
            SnakeDirection direction = snakeDirectionFromProto(snake.getHeadDirection());

            SnakeEntity snakeEntity = new SnakeEntity();
            snakeEntity.id = id;
            snakeEntity.state = state;
            snakeEntity.body = body;
            snakeEntity.direction = direction;
            snakes.put(id, snakeEntity);
        }

        Vector<Point> foods = new Vector<>();
        for (var food: protoState.getFoodsList()) {
            foods.add(snakePointFromProto(food));
        }

        return new SnakeGameState(snakes, foods, stateNumber);
    }

    private static SnakeDirection snakeDirectionFromProto(Direction headDirection) {
        return switch (headDirection) {
            case UP -> SnakeDirection.UP;
            case DOWN -> SnakeDirection.DOWN;
            case LEFT -> SnakeDirection.LEFT;
            case RIGHT -> SnakeDirection.RIGHT;
        };
    }

    private static SnakeEntity.SnakeState snakeStateFromProto(GameState.Snake.SnakeState state) {
        return switch (state) {
            case ALIVE -> SnakeEntity.SnakeState.ALIVE;
            case ZOMBIE -> SnakeEntity.SnakeState.ZOMBIE;
        };
    }

    private static Point snakePointFromProto(GameState.Coord p) {
        return new Point(p.getX(), p.getY());
    }

    public static byte[] writeStateMessage(int meg_seq, SnakeGameState gameState, HashMap<Integer, SnakePlayer> peers) {
        GameState protoState = GameState.newBuilder()
                .setStateOrder(gameState.getOrder())
                .buildPartial();

        for (var snake: gameState.getSnakes().values()) {
            GameState.Snake protoSnake = GameState.Snake.newBuilder()
                    .setState(snakeStateToProto(snake.getState()))
                    .setHeadDirection(snakeDirectionToProto(snake.getDirection()))
                    .setPlayerId(snake.id)
                    .build();
            for (var p: snake.getBody()) {
                protoSnake = protoSnake.toBuilder().addPoints(snakePointToProto(p)).build();
            }
            protoState = protoState.toBuilder().addSnakes(protoSnake).buildPartial();
        }

        for (var p: gameState.getFoods()) {
            protoState = protoState.toBuilder().addFoods(snakePointToProto(p)).buildPartial();
        }

        protoState = protoState.toBuilder().setPlayers(writeGamePlayers(peers)).buildPartial();

        GameMessage.StateMsg stateMsg = GameMessage.StateMsg.newBuilder()
                .setState(protoState)
                .build();

        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(meg_seq)
                .setState(stateMsg)
                .build();


        return gameMessage.toByteArray();
    }

    private static GameState.Coord snakePointToProto(Point p) {
        return GameState.Coord.newBuilder()
                .setX(p.x)
                .setY(p.y)
                .build();
    }

    private static Direction snakeDirectionToProto(SnakeDirection direction) {
        return switch (direction) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
        };
    }

    private static GameState.Snake.SnakeState snakeStateToProto(SnakeEntity.SnakeState state) {
        return switch (state) {
            case ALIVE -> GameState.Snake.SnakeState.ALIVE;
            case ZOMBIE -> GameState.Snake.SnakeState.ZOMBIE;
        };
    }

    public static byte[] writeSteerMsg(int msg_seq, SnakeDirection dir) {
        GameMessage.SteerMsg steerMsg = GameMessage.SteerMsg.newBuilder()
                .setDirection(snakeDirectionToProto(dir))
                .build();

        GameMessage gameMessage = GameMessage.newBuilder()
                .setMsgSeq(msg_seq)
                .setSteer(steerMsg)
                .build();

        return gameMessage.toByteArray();
    }

    public static SnakeSteer readSteerMsg(byte[] data) throws InvalidProtocolBufferException {
        GameMessage.SteerMsg steerMsg = GameMessage.parseFrom(data).getSteer();

        return new SnakeSteer(0, null, 0, snakeDirectionFromProto(steerMsg.getDirection()));
    }
}
