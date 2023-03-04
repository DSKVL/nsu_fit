import NodeRole;

class GamePlayer (
    var id: Int = 0
    var name: String? = null
    var address: java.net.InetAddress? = null
    var port: Int = 0
    var role: NodeRole? = null
    var playerType: Int = 0
    var score: Int = 0
)