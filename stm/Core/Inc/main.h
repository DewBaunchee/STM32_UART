/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.h
  * @brief          : Header for main.c file.
  *                   This file contains the common defines of the application.
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; Copyright (c) 2022 STMicroelectronics.
  * All rights reserved.</center></h2>
  *
  * This software component is licensed by ST under BSD 3-Clause license,
  * the "License"; You may not use this file except in compliance with the
  * License. You may obtain a copy of the License at:
  *                        opensource.org/licenses/BSD-3-Clause
  *
  ******************************************************************************
  */
/* USER CODE END Header */

/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __MAIN_H
#define __MAIN_H

#ifdef __cplusplus
extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "stm32f1xx_hal.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */

/* USER CODE END Includes */

/* Exported types ------------------------------------------------------------*/
/* USER CODE BEGIN ET */
typedef uint16_t Level;
typedef uint8_t Mode;
typedef uint8_t Command;
typedef uint8_t RgbSource;
/* USER CODE END ET */

/* Exported constants --------------------------------------------------------*/
/* USER CODE BEGIN EC */
extern RgbSource rgbSource;
extern Mode mode;
extern Level levels[3];
extern Level thresholds[6];
/* USER CODE END EC */

/* Exported macro ------------------------------------------------------------*/
/* USER CODE BEGIN EM */

// MODES
#define MODE_AUTO 0
#define MODE_MANUAL 1

// TYPES
#define ROTATION 0
#define LIGHT 1
#define TEMP 2

// COMMANDS

#define NO_COMMAND 0

#define NEXT_VALUE 1

#define SET_MODE_MANUAL 2
#define SET_MODE_AUTO 3

#define SET_RGB_ON_ROTATION 4
#define SET_RGB_ON_LIGHT 5
#define SET_RGB_ON_TEMP 6

/* USER CODE END EM */

/* Exported functions prototypes ---------------------------------------------*/
void Error_Handler(void);

/* USER CODE BEGIN EFP */

void updateIntensity();
void transmitLevels();

void high();
void normal();
void low();

/* USER CODE END EFP */

/* Private defines -----------------------------------------------------------*/
#define LDR_Pin GPIO_PIN_1
#define LDR_GPIO_Port GPIOA
#define LED_GREEN_Pin GPIO_PIN_7
#define LED_GREEN_GPIO_Port GPIOA
#define LED_RED_Pin GPIO_PIN_7
#define LED_RED_GPIO_Port GPIOC
#define TMS_Pin GPIO_PIN_13
#define TMS_GPIO_Port GPIOA
#define TCK_Pin GPIO_PIN_14
#define TCK_GPIO_Port GPIOA
#define SWO_Pin GPIO_PIN_3
#define SWO_GPIO_Port GPIOB
#define LED_BLUE_Pin GPIO_PIN_6
#define LED_BLUE_GPIO_Port GPIOB
/* USER CODE BEGIN Private defines */

/* USER CODE END Private defines */

#ifdef __cplusplus
}
#endif

#endif /* __MAIN_H */
