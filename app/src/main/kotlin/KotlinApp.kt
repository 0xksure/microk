package raft

import io.grpc.Server
import io.grpc.ServerBuilder
import raft.RaftOuterClass.VoteRequest
import raft.RaftOuterClass.VoteResponse

class RaftServer(private val port: Int){
    val currentTerm = 0
    val server: Server = ServerBuilder.forPort(port).addService(RaftService()).build()

    fun start(){
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                this@RaftServer.stop()
                println("*** server shut down ")
            }
        )
    }

    fun stop(){
        server.shutdown()
    }

    fun blockUntilShutdown(){
        server.awaitTermination()
    }
    internal class RaftService(): RaftGrpcKt.RaftCoroutineImplBase(){


        /**
         * requestVote can be called to request vote from the node
         */
       override suspend fun requestVote(request: VoteRequest): VoteResponse {
           if(request.term < this.currentTerm)
           return voteResponse {
               this.term = 1
               this.voteGranted = true
           }
       }

        /**
         * appendEntries is called when new entries should be added
         */
        override suspend fun appendEntries(request: RaftOuterClass.EntryRequest): RaftOuterClass.EntryResponse {
            return entryResponse {
                this.term = 1
                this.success = true
            }
        }
    }
}

fun main() {
    val port= 8900
    val routeGuideServer = RaftServer(port)
    routeGuideServer.start()
    routeGuideServer.blockUntilShutdown()
}