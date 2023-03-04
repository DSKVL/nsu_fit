class JoinRequest (
    var playerName: String? = null
    var gameName: String? = null
    var role: SnakeRole? = null
    var playerType: Int = 0
    var senderAddress: java.net.InetAddress? = null
    var senderPort: Int = 0
)