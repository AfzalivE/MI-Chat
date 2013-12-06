//
//  MessageCell.h
//  michat
//
//  Created by Afzal Najam on 2013-09-24.
//  Copyright (c) 2013 Afzal Najam. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MessageCell : UITableViewCell

@property (nonatomic, strong) IBOutlet UILabel *userNameLabel;
@property (nonatomic, strong) IBOutlet UILabel *messageLabel;
@property (nonatomic, strong) IBOutlet UILabel *timeLabel;

@end
