package com.gwz.dockerexp.Actors.RaphtoryActors

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.Actor
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import com.gwz.dockerexp.caseclass.{LiveAnalysis, Results}

import scala.concurrent.duration._



class LiveAnalysisManager(managerCount:Int) extends Actor{
  val mediator = DistributedPubSub(context.system).mediator
  mediator ! DistributedPubSubMediator.Put(self)

  override def preStart() { //set up partition to report how many messages it has processed in the last X seconds
    println("Prestarting")
    context.system.scheduler.schedule(Duration(10, SECONDS),Duration(10, SECONDS),self,"analyse")
  }

  //************* MESSAGE HANDLING BLOCK
  override def receive: Receive = {
    case "analyse" => analyse()
    case Results(result) => println(result)
  }

  def analyse() ={
      println("Hello")
      for(i <- 0 until managerCount){
        println(s"inside $i")
        mediator ! DistributedPubSubMediator.Send(s"/user/Manager_$i",LiveAnalysis(),false)
      }
  }

}

