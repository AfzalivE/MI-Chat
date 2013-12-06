//
//  Message.h
//  michat
//
//  Created by Afzal Najam on 2013-09-24.
//  Copyright (c) 2013 Afzal Najam. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Message : NSObject

@property (nonatomic, copy) NSString *userName;
@property (nonatomic, copy) NSString *message;
@property (nonatomic, copy) NSString *time;

// add more properties later

@end
