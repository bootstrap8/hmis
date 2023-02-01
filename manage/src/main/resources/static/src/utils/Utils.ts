import {ElNotification, ElMessage} from 'element-plus'
import type {EpPropMergeType} from "element-plus/es/utils/vue/props/types"

export function jsonPretty(str: string, tab?: number | undefined): string {
  const obj = JSON.parse(str)
  return tab == undefined ?
      JSON.stringify(obj, null, 2) :
      JSON.stringify(obj, null, tab);
}

export function notify(title: string, message: string,
                       type: EpPropMergeType<StringConstructor,
                           "success" | "warning" | "info" | "error", unknown>): void {
  ElNotification({
    title: '操作',
    message: '操作成功',
    type: type,
    position: "bottom-right"
  })
}

export function msg(message: string,
                    type: EpPropMergeType<StringConstructor,
                        "success" | "warning" | "info" | "error", unknown>): void {
  ElMessage({
    message: message,
    type: type,
  })
}

export function objectEmpty(obj: any): boolean {
  return obj && Object.keys(obj).length == 0
}

export class Page {
  pageNum: number;
  pageSize: number;
  total: number;

  constructor(pageNum: number, pageSize: number, total: number) {
    this.pageNum = pageNum;
    this.pageSize = pageSize;
    this.total = total;
  }

}
