//
//  AppDelegate.m
//  michat
//
//  Created by Afzal Najam on 2013-09-23.
//  Copyright (c) 2013 Afzal Najam. All rights reserved.
//

#import "AppDelegate.h"
#import "MessagesViewController.h"
#import "Message.h"

@implementation AppDelegate {
    NSMutableArray *messages;
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  // Override point for customization after application launch.
  messages = [NSMutableArray arrayWithCapacity:20];
  Message *message = [[Message alloc] init];
  message.message = @"test";
  message.userName = @"username";
  message.time = @"2 sec ago";
  
  [messages addObject:message];
  
  message = [[Message alloc] init];
  message.message = @"test2";
  message.userName = @"username2";
  message.time = @"7 sec ago";
  
  [messages addObject:message];
  
  message = [[Message alloc] init];
  message.message = @"test3";
  message.userName = @"username3";
  message.time = @"12 sec ago";

  [messages addObject:message];
  
  UINavigationController *navigationController = (UINavigationController *) self.window.rootViewController;
  MessagesViewController *messagesViewController = (MessagesViewController *) [[navigationController viewControllers] objectAtIndex:0];
  messagesViewController.messages = messages;
  
  return YES;
}
							
- (void)applicationWillResignActive:(UIApplication *)application
{
  // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
  // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
  // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
  // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
  // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
  // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
  // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
